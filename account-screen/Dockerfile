FROM node:14.8
WORKDIR /app
ENV PATH /app/node_modules/.bin:${PATH}
COPY package.json ./
COPY yarn.lock ./
RUN npm install
RUN npm install react-scripts@3.4.1 -g
COPY . ./
ENTRYPOINT [ "npm", "start" ]
