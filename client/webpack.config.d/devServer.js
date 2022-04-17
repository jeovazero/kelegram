const webpack = require("webpack")
config.devServer = {
  ...config.devServer,
  "historyApiFallback": true
}

const definePlugin = new webpack.DefinePlugin(
   {
      SERVER: JSON.stringify(process.env.SERVER || "localhost:8000")
   }
)

config.plugins = [
    ...config.plugins,
    definePlugin
]
