* [x] Criar vo que serve para parsear do json e yaml ao mesmo tempo
* [x] Criar os parsers
  * [x] YAML
  * [x] JSON
  * [x] ENV
* [ ] Testar o merge dos 3 parsers no `COnfigV3Service`
* [ ] Parsers precisam saber ler arquivo do diretorio de configuração e só ler se a versão for 3, senão retornar nulo
* [ ] Testar que ConfigService vai tentar ler a v1,v2,v3 e usar o que achar primeiro nessa ordem 
* [ ] Criar a config na v3 se não encontrar nenhuma, caso encontre, mantém na versão encontrada
* [ ] Update the docs
  * [ ] Env
  * [ ] JSON
  * [ ] Yaml support

