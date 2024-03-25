namespace com.github.hummel.mpp.lab4;

using Microsoft.CodeAnalysis.CSharp.Syntax;
using Microsoft.CodeAnalysis.CSharp;
using System.Text;
using Microsoft.CodeAnalysis;
using Microsoft.CodeAnalysis.CSharp.Formatting;
using Formatter = Microsoft.CodeAnalysis.Formatting.Formatter;
using System.Collections.Concurrent;

using static Microsoft.CodeAnalysis.CSharp.SyntaxFactory;

public class Generator
{
    public async Task<ConcurrentDictionary<string, string>> generateTestClasses(string text)
    {
        return await parseFileForClasses(text);
    }

    private async Task<ConcurrentDictionary<string, string>> parseFileForClasses(string text)
    {
        var syntaxTree = CSharpSyntaxTree.ParseText(text);
        var root = syntaxTree.GetRoot();
        var compilation = CSharpCompilation.Create("MyCompilation").AddSyntaxTrees(syntaxTree);
        var semanticModel = compilation.GetSemanticModel(syntaxTree);
        var classes = root.DescendantNodes().OfType<ClassDeclarationSyntax>();
        var map = new ConcurrentDictionary<string, string>();
        Parallel.ForEach(classes, clazz =>
        {
            map.TryAdd(clazz.Identifier.ValueText, viewOf(generate(clazz, semanticModel)));
        });
        return map;
    }

    private string viewOf(CompilationUnitSyntax compilationUnitSyn)
    {
        var workspace = new AdhocWorkspace();
        var options = workspace.Options;
        options = options.WithChangedOption(CSharpFormattingOptions.NewLinesForBracesInMethods, false);
        options = options.WithChangedOption(CSharpFormattingOptions.NewLinesForBracesInTypes, false);
        var formattedNode = Formatter.Format(compilationUnitSyn, workspace, options);
        var stringBuilder = new StringBuilder();
        using (var stringWriter = new StringWriter(stringBuilder))
        {
            formattedNode.WriteTo(stringWriter);
        }

        //Console.WriteLine(stringBuilder.ToString());
        return stringBuilder.ToString();
    }

    public CompilationUnitSyntax generate(ClassDeclarationSyntax Class, SemanticModel semanticModel)
    {
        var compilationUnit = CompilationUnit()
        .AddUsings(UsingDirective(IdentifierName("System")))
        .AddUsings(UsingDirective(IdentifierName("System.Generic")))
        .AddUsings(UsingDirective(IdentifierName("System.Collections.Generic")))
        .AddUsings(UsingDirective(IdentifierName("System.Linq")))
        .AddUsings(UsingDirective(IdentifierName("System.Text")))
        .AddUsings(UsingDirective(IdentifierName("System.Moq")))
        .AddUsings(UsingDirective(IdentifierName("NUnit.Framework")));

        var constructorSyns = Class.DescendantNodes().OfType<ConstructorDeclarationSyntax>();
        ConstructorDeclarationSyntax? savedConstructorSyn = null;
        List<ParameterSyntax>? savedParameterSyns = null;
        var interfaceMembersMaxAmount = -1;
        var interfaceMembersAmount = 0;
        if (constructorSyns.Any())
        {
            foreach (var constructorSyn in constructorSyns)
            {
                var tempParameterSyns = new List<ParameterSyntax>();
                interfaceMembersAmount = 0;
                foreach (var parameterSyn in constructorSyn.ParameterList.Parameters)
                {
                    var parameterSymbol = semanticModel.GetDeclaredSymbol(parameterSyn);
                    if ((parameterSymbol!.Type.TypeKind == TypeKind.Interface) ||
                        (parameterSymbol.Type.Name.Length > 2 && parameterSymbol.Type.Name[0] == 'I' && char.IsUpper(parameterSymbol.Type.Name[1])))
                    {
                        tempParameterSyns.Add(parameterSyn);
                        interfaceMembersAmount++;
                    }
                }
                if (constructorSyn.Modifiers.Any(modifier => modifier.IsKind(SyntaxKind.PublicKeyword)))
                {
                    if (interfaceMembersAmount > interfaceMembersMaxAmount)
                    {
                        interfaceMembersMaxAmount = interfaceMembersAmount;
                        savedConstructorSyn = constructorSyn;
                        savedParameterSyns = tempParameterSyns;
                    }
                }
            }
        }
        else
        {
            savedConstructorSyn = ConstructorDeclaration(Identifier(Class.Identifier.ValueText))
                .WithModifiers(TokenList(Token(SyntaxKind.PublicKeyword)))
                .WithBody(Block());
            savedParameterSyns = new List<ParameterSyntax>();
        }

        var namespaceSyn = NamespaceDeclaration(IdentifierName("Tests"));
        var classSyn = generateTestClass(Class.Identifier.ValueText);
        var parameterSynQuantity = 1;
        foreach (var parameterSyn in savedParameterSyns!)
        {
            classSyn = classSyn.AddMembers(generateField($"Mock<{parameterSyn.Type}>", $"_dependency{parameterSynQuantity}"));
            parameterSynQuantity++;
        }
        classSyn = classSyn.AddMembers(generateField(Class.Identifier.ValueText, $"_myClassUnderTest"));
        classSyn = classSyn.AddMembers(generateSetUpMethod(savedConstructorSyn!, semanticModel, Class));
        var publicMethods = Class.Members.OfType<MethodDeclarationSyntax>().Where(method => method.Modifiers.Any(modifier => modifier.IsKind(SyntaxKind.PublicKeyword)));
        foreach (var publicMethod in publicMethods)
        {
            classSyn = classSyn.AddMembers(generateTestMethod(publicMethod, semanticModel));
        }
        namespaceSyn = namespaceSyn.AddMembers(classSyn);
        compilationUnit = compilationUnit.AddMembers(namespaceSyn);
        return compilationUnit;
    }

    private FieldDeclarationSyntax generateField(string type, string name)
    {
        var variableSyn = VariableDeclaration(IdentifierName(type))
            .AddVariables(VariableDeclarator(Identifier(name)));
        var fieldSyn = FieldDeclaration(variableSyn)
            .AddModifiers(Token(SyntaxKind.PrivateKeyword));
        return fieldSyn;
    }

    private MethodDeclarationSyntax generateTestMethod(MethodDeclarationSyntax methodSyn, SemanticModel semanticModel)
    {
        var statementSyns = new List<StatementSyntax>();
        var argumentSyns = new List<ArgumentSyntax>();

        var parameterSyns = methodSyn.ParameterList.Parameters;

        foreach (var parameterSyn in parameterSyns)
        {
            var parameterSymbol = semanticModel.GetDeclaredSymbol(parameterSyn);
            if ((parameterSymbol!.Type.TypeKind == TypeKind.Interface) || (parameterSymbol.Type.Name.Length > 2 && parameterSymbol.Type.Name[0] == 'I' && char.IsUpper(parameterSymbol.Type.Name[1])))
            {
                var statementSyn = generateMoqType(parameterSyn.Type!, parameterSyn.Identifier.ValueText);
                var argumentSyn = Argument(MemberAccessExpression(SyntaxKind.SimpleMemberAccessExpression,
                                IdentifierName(parameterSyn.Identifier.ValueText),
                                IdentifierName("Object")));
                argumentSyns.Add(argumentSyn);
                statementSyns.Add(statementSyn);
            }
            else
            {
                var variableSyn = generatePrimitiveType(parameterSyn.Type!, parameterSyn.Identifier.ValueText);
                var argumentSyn = Argument(IdentifierName(parameterSyn.Identifier.ValueText));
                argumentSyns.Add(argumentSyn);
                statementSyns.Add(LocalDeclarationStatement(variableSyn));
            }
        }
        var argumentList = ArgumentList(SeparatedList(argumentSyns));
        if (methodSyn.ReturnType.ToString() == "void")
        {
            var statementSyn = ExpressionStatement(
            InvocationExpression(
                MemberAccessExpression(
                    SyntaxKind.SimpleMemberAccessExpression,
                    IdentifierName("Assert"),
                    IdentifierName("DoesNotThrow")))
            .WithArgumentList(
                ArgumentList(
                    SingletonSeparatedList(
                        Argument(
                            ParenthesizedLambdaExpression()
                            .WithBlock(
                                Block(
                                    SingletonList<StatementSyntax>(
                                        ExpressionStatement(
                                            InvocationExpression(
                                                MemberAccessExpression(
                                                    SyntaxKind.SimpleMemberAccessExpression,
                                                    IdentifierName("_myClassUnderTest"),
                                                    IdentifierName(methodSyn.Identifier.ValueText)))
                                            .WithArgumentList(argumentList))))))))));
            statementSyns.Add(statementSyn);
        }
        else
        {
            var typeSyntax = methodSyn.ReturnType;
            var variableSyn = VariableDeclaration(
                typeSyntax,
                SingletonSeparatedList(
                VariableDeclarator(
                        Identifier("actual")
                    ).WithInitializer(
                        EqualsValueClause(
                            InvocationExpression(
                                MemberAccessExpression(
                                    SyntaxKind.SimpleMemberAccessExpression,
                                    IdentifierName("_myClassUnderTest"),
                                    IdentifierName(methodSyn.Identifier.ValueText)))
                            .WithArgumentList(argumentList)))));
            statementSyns.Add(LocalDeclarationStatement(variableSyn));
            var statementSyn1 = generatePrimitiveType(typeSyntax, "expected");
            statementSyns.Add(LocalDeclarationStatement(statementSyn1));
            var statementSyn2 = ExpressionStatement(
                InvocationExpression(
                    MemberAccessExpression(
                        SyntaxKind.SimpleMemberAccessExpression,
                        IdentifierName("Assert"),
                        IdentifierName("That")))
                .WithArgumentList(
                    ArgumentList(
                        SeparatedList<ArgumentSyntax>(
                            new SyntaxNodeOrToken[]{
                                    Argument(
                                        IdentifierName("actual")),
                                    Token(SyntaxKind.CommaToken),
                                    Argument(
                                        InvocationExpression(
                                            MemberAccessExpression(
                                                SyntaxKind.SimpleMemberAccessExpression,
                                                IdentifierName("Is"),
                                                IdentifierName("EqualTo")))
                                        .WithArgumentList(
                                            ArgumentList(
                                                SingletonSeparatedList(
                                                    Argument(
                                                        IdentifierName("expected"))))))}))));
            statementSyns.Add(statementSyn2);
        }
        var expressionStatementSyn = ExpressionStatement(
                        InvocationExpression(
                            MemberAccessExpression(
                                SyntaxKind.SimpleMemberAccessExpression,
                                IdentifierName("Assert"),
                                IdentifierName("Fail")))
                        .WithArgumentList(
                            ArgumentList(
                                SingletonSeparatedList(
                                    Argument(
                                        LiteralExpression(
                                            SyntaxKind.StringLiteralExpression,
                                            Literal("autogenerated")))))));
        statementSyns.Add(expressionStatementSyn);
        var methodSynResult = MethodDeclaration(
            PredefinedType(Token(SyntaxKind.VoidKeyword)),
            Identifier(methodSyn.Identifier.ValueText + "Test"))
            .AddModifiers(Token(SyntaxKind.PublicKeyword))
            .AddAttributeLists(
                AttributeList(SingletonSeparatedList(
                Attribute(IdentifierName("Test")))))
            .WithBody(Block(statementSyns));
        return methodSynResult;
    }

    private ClassDeclarationSyntax generateTestClass(string name)
    {
        var classSyn = ClassDeclaration(name + "Tests")
            .AddModifiers(Token(SyntaxKind.PublicKeyword))
            .WithAttributeLists(
            SingletonList(
                AttributeList(
                    SingletonSeparatedList(
                        Attribute(
                            IdentifierName("TestFixture")
                        )
                    )
                )
            )
        );
        return classSyn;
    }

    private MethodDeclarationSyntax generateSetUpMethod(ConstructorDeclarationSyntax constructorSyn, SemanticModel semanticModel, ClassDeclarationSyntax classSyn)
    {
        var statementSyns = new List<StatementSyntax>();
        var argumentSyns = new List<ArgumentSyntax>();
        int ifaceQuantity = 1;
        int paramQuantity = 1;
        var parameterSyns = constructorSyn.ParameterList.Parameters;
        foreach (var parameterSyn in parameterSyns)
        {
            var parameterSymbol = semanticModel.GetDeclaredSymbol(parameterSyn);
            if ((parameterSymbol!.Type.TypeKind == TypeKind.Interface) ||
                (parameterSymbol.Type.Name.Length > 2 && parameterSymbol.Type.Name[0] == 'I' && char.IsUpper(parameterSymbol.Type.Name[1])))
            {
                var statementSyn = generateMoqType(parameterSyn.Type!, $"_dependency{ifaceQuantity}");
                var argumentSyn = Argument(MemberAccessExpression(SyntaxKind.SimpleMemberAccessExpression,
                                IdentifierName($"_dependency{ifaceQuantity}"),
                                IdentifierName("Object")));
                argumentSyns.Add(argumentSyn);
                statementSyns.Add(statementSyn);
                ifaceQuantity++;
            }
            else
            {
                var variableSyn = generatePrimitiveType(parameterSyn.Type!, $"param{paramQuantity}");
                var argumentSyn = Argument(IdentifierName($"param{paramQuantity}"));
                argumentSyns.Add(argumentSyn);
                paramQuantity++;
                statementSyns.Add(LocalDeclarationStatement(variableSyn));
            }
        }
        var argumentList = ArgumentList(SeparatedList(argumentSyns));

        var expressionStatementSyn = ExpressionStatement(
            AssignmentExpression(
                SyntaxKind.SimpleAssignmentExpression,
                IdentifierName("_myClassUnderTest"),
                ObjectCreationExpression(
                    IdentifierName(classSyn.Identifier.ValueText))
                .WithArgumentList(argumentList)));
        statementSyns.Add(expressionStatementSyn);
        var methodSynResult = MethodDeclaration(
            PredefinedType(Token(SyntaxKind.VoidKeyword)),
            Identifier("SetUp"))
            .AddModifiers(Token(SyntaxKind.PublicKeyword))
            .AddAttributeLists(
                AttributeList(SingletonSeparatedList(
                Attribute(IdentifierName("SetUp")))))
            .WithBody(Block(statementSyns));
        return methodSynResult;
    }

    private VariableDeclarationSyntax generatePrimitiveType(TypeSyntax typeSyn, string name)
    {
        return VariableDeclaration(
            typeSyn,
            SingletonSeparatedList(
                VariableDeclarator(
                    Identifier(name)
                ).WithInitializer(
                    EqualsValueClause(
                        DefaultExpression(typeSyn)
                    )
                )
            )
        );
    }

    private ExpressionStatementSyntax generateMoqType(TypeSyntax typeSyn, string name)
    {
        return ExpressionStatement(
            AssignmentExpression(
                SyntaxKind.SimpleAssignmentExpression,
                IdentifierName(name),
                ObjectCreationExpression(
                    GenericName(
                        Identifier("Mock"))
                    .WithTypeArgumentList(
                        TypeArgumentList(
                            SingletonSeparatedList(
                                typeSyn))))
                .WithArgumentList(
                    ArgumentList())));
    }
}