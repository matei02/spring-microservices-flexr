# Use Node.js 20 alpine as the base image
FROM node:20-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci

# Copy build artifacts from CI pipeline
COPY . .
COPY .next ./.next

# Start the Next.js application
CMD ["npm", "start"]

# Expose port 3000
EXPOSE 3000
