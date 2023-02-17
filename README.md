# Tubes 1 Strategi Algoritma: Algoritma Greedy untuk Game Galaxio

## Penjelasan Algoritma Greedy
Algoritma greedy yang kami implementasikan merupakan strategi greedy by density.
Kami membuat command-command yang dapat dijalankan oleh bot.
Setiap command tersebut masing-masing memiliki atribut `profit` dan `dangerLevel`.
Kedua atribut tersebut disesuaikan nilainya berdasarkan keadaan dan situasi di dalam game.
Nantinya command-command tersebut diseleksi berdasarkan nilai desity: rasio `profit` / `dangerLevel` terbesar.
Namun, jika command yang telah terpilih memiliki `dangerLevel` bernilai `EXTREME`, maka command dengan density terbesar selanjutnya akan dipilih.

## _Requirement_ Program
Agar dapat menjalankan game dengan lancar, dibutuhkan program-program berikut ini
1. [Java 11+](https://www.oracle.com/id/java/technologies/javase/jdk11-archive-downloads.html)
2. [Maven](https://maven.apache.org/)
3. [.NET Core 3.1](https://dotnet.microsoft.com/en-us/download/dotnet/3.1)
4. [.NET Core 5.0](https://dotnet.microsoft.com/en-us/download/dotnet/5.0)

## Langkah-langkah Kompilasi
Untuk mengkompilasi program bot, ikuti langkah-langkah berikut.
```shell
$ git clone https://github.com/Mifkiyan/Tubes1_Bottan
$ cd Tubes1_Bottan
$ mvn clean package
```
Setelah itu, file jar hasil kompilasi akan berada di folder target dengan nama `Bottan.jar`.
Selamat mengadu domba bot :smile:

## Author
| NIM | Nama |
------|--------
| 13521075 | Muhammad Rifko Favian |
| 13521083 | Moch. Sofyan Firdaus |
| 13521098 | Fazel Ginanda |
