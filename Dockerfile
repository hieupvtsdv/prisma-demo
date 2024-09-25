FROM node:14

RUN apt-get update
WORKDIR /app
COPY package*.json ./
RUN npm install

ENV POSTGRES_PASSWORD=12345

COPY server.js .
EXPOSE 3000

CMD [ "node", "server.js" ]
