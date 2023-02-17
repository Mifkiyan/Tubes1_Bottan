# Tubes 1 Strategi Algoritma: Algoritma Greedy untuk Game Galaxio

## Penjelasan Algoritma Greedy
Algoritma greedy yang kami implementasikan merupakan strategi greedy by density.
Kami membuat command-command yang dapat dijalankan oleh bot.
Setiap command tersebut masing-masing memiliki atribut `profit` dan `dangerLevel`.
Kedua atribut tersebut disesuaikan nilainya berdasarkan keadaan dan situasi di dalam game.
Nantinya command-command tersebut diseleksi berdasarkan nilai desity: rasio `profit` / `dangerLevel` terbesar.
Namun, jika command yang telah terpilih memiliki `dangerLevel` bernilai `EXTREME`, maka command dengan density terbesar selanjutnya akan dipilih.

