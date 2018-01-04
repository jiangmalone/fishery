const path = require('path');
var pxtorem = require('postcss-pxtorem');
const svgSpriteDirs = [
  require.resolve('antd-mobile').replace(/warn\.js$/, ''), // antd-mobile 内置svg
  path.resolve(__dirname, 'src/assets'),  // 业务代码本地私有 svg 存放目录
];
export default {
  entry: "src/index.js",
  disableCSSModules: true,
  less: true,
  env: {
    development: {
      extraBabelPlugins: [
        "dva-hmr",
        "transform-runtime",
        ["import", { "libraryName": "antd-mobile", 'libraryDirectory': 'lib', "style": true }]
      ],
      extraPostCSSPlugins: [
        pxtorem({
          rootValue: 100,
          propWhiteList: [],
        }),
      ],
      proxy: {
        "/api": {
          "target": "http://172.17.3.103:8080/frishery/api/",//小轩
          // "target": "http://172.17.3.158:8081/freelycar_wechat/api/",//小付

          // "target": "http://localhost:8081/freelycar_wechat/api/",  //localhost

          "changeOrigin": true,
          "pathRewrite": { "^/api": "" }
        }
      }
    },
    production: {
      extraBabelPlugins: [
        "transform-runtime",
        ["import", { "libraryName": "antd-mobile", 'libraryDirectory': 'lib', "style": true }]
      ],
      extraPostCSSPlugins: [
        pxtorem({
          rootValue: 100,
          propWhiteList: [],
        }),
      ]
    }
  },
  publicPath: "http://www.freelycar.com/freelycar_wechat/",
  hash: false,

  autoprefixer: {
    "browsers": [
      "iOS >= 8", "Android >= 4"
    ]
  },
  svgSpriteLoaderDirs: svgSpriteDirs,

}
