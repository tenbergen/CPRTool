FROM node

WORKDIR /usr/src/cpr-tool

COPY package.json .

RUN npm install

COPY src ./src

CMD ["node", "src/index.js"]
