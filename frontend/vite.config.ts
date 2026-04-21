import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  test: {
    environment: "jsdom",
    setupFiles: "./src/app/test/setup.ts",
    maxWorkers: 1,
    minWorkers: 1
  }
});
