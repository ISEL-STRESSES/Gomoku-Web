const ESLintPlugin = require('eslint-webpack-plugin');
const { resolve } = require('path');
module.exports = {
  mode: 'development',
  devServer: {
    port: 8000,
    historyApiFallback: true,
  },
  resolve: {
    extensions: ['.js', '.ts', '.tsx', '.css', '.ico', '.html'],
  },
  plugins: [
    new ESLintPlugin({
      extensions: ['js', 'jsx', 'ts', 'tsx'],
    }),
  ],
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: 'ts-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.css$/i,
        use: ['style-loader', 'css-loader'],
        exclude: /node_modules/,
      },
      {
        test: /\.(png|svg|jpg|jpeg|gif|ico)$/i,
        exclude: /node_modules/,
        use: ['file-loader?name=[name].[ext]'],
      },
    ],
  },
  output: {
    filename: 'main.js',
    path: resolve(__dirname, 'dist'), // Output directory
  },
};
