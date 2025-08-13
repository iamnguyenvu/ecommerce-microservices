# Step 1: Build frontend
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build

# Step 2: Serve with nginx
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
