# Use the official Node.js 18 image
FROM node:16.20.2

# Set working directory
WORKDIR /usr/src/app

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy the application code and build the production files
COPY . .
RUN npm run build

# Install serve to serve the production build
RUN npm install -g serve

# Expose port 3000
EXPOSE 3000

# Serve the production build
CMD ["serve", "-s", "build"]