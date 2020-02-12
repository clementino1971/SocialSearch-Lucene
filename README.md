# SocialSearch-Lucene

1-Baixar o Lucene.</br>
https://lucene.apache.org/core/downloads.html


2- Adicionar os seguintes <b>.jar</b> ao CLASSPATH:
 * core/lucene-core-8.4.1.jar
 * queryparser/lucene-queryparser-8.4.1.jar
 * analysis/common/lucene-analyzers-common-8.4.1.jar
 * demo/lucene-demo-8.4.1.jar


 Esses acima estão disponíveis dentro das pastas do lucene, o <b>.jar</b> abaixo está disponível em http://www.java2s.com/Code/Jar/j/Downloadjsonsimple11jar.htm.
 
 * json-simple-1.1.jar
 
3 - Primeiro executa-se o Indexador, o qual vai criar uma pasta <b> index/ </b> contendo os dados indexados.

4 - Depois executa o Buscador para realizar a busca.

### Compila
javac Indexador.java
### Executa
java Indexador
