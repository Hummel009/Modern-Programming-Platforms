FROM node:alpine

EXPOSE 3000

WORKDIR /usr/app/front
COPY ./ ./

RUN npm install

CMD ["npm", "start"]