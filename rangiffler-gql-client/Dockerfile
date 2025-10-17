FROM node:22.6.0-alpine AS build
ARG NPM_COMMAND
ARG VERSION
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . ./
RUN npm run ${NPM_COMMAND}

FROM nginx:1.27.1-alpine AS release
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html/
LABEL version=${VERSION}
ENV TZ=UTC+4
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]