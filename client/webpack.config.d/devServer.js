const webpack = require("webpack")
config.devServer = {
  ...config.devServer,
  "historyApiFallback": true
}

const definePlugin = new webpack.DefinePlugin(
   {
      SERVER: JSON.stringify(process.env.SERVER || "http://localhost:8000"),
      ENV:  JSON.stringify(process.env.NODE_ENV || "development")
   }
)

config.plugins = [
    ...config.plugins,
    definePlugin
]
