# JapaneseChat

これは、[Google CGI API for Japanese Input](https://www.google.co.jp/ime/cgiapi.html)を用いて、ローマ字で書かれたメッセージを日本語に変換する、シンプルなプラグインです。

## ライセンス

このプロジェクトのライセンスは[Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0)です。

## ビルドする

このリポジトリをクローンしたあと、  
サーバー導入用のjarファイルを生成したい場合は`./gradlew pluginjar`を、  
各種テスト(checkstyleや単体テスト)を実行したい場合は`./gradlew build`を実行してください。
