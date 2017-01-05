# scrypto-benchmarks

Thi repository contains benchmarks for authenticated AVL+ trees and other features of the Scrypto framework(only the former at the moment).
You can find some ready results for our hardware in the paper [Improving Authenticated Dynamic Dictionaries, with Applications to Cryptocurrencies](https://eprint.iacr.org/2016/994).

## Prerequisites

Java 8 and SBT (Scala Build Tool) are required. Scala 2.12.x and other
dependencies will be downloaded by the SBT.

## Authenticated AVL+ trees

There are two implementations of the AVL+ trees defined in the paper 
[https://eprint.iacr.org/2016/994](https://eprint.iacr.org/2016/994), 
legacy one (from *scorex.crypto.authds.avltree.legacy* package) operates 
without batch updates and also does not support deletion of an element 
from a tree, while newer implementation (from the *scorex.crypto.authds.avltree.batch*) 
supports efficient batch updates and also removals. 

## Benchmarks List

### Performance Benchmark

The benchmark shows performance of the AVL+ (legacy version) in comparison 
with authenticated treaps. To get the data, launch the benchmark with:

    sbt "run-main scorex.crypto.benchmarks.PerformanceMeter"


### Batching Benchmark

The benchmark prints out sizes and generation/verification times for 
batching and legacy provers/verifiers. A tree is initially populated. 

    sbt "run-main scorex.crypto.benchmarks.BatchingBenchmark"
    
 The output table contains following data in the rows:
 
 * *Step* is a batch size
 * *Plain size* is an average proof size with no batching compression
 * *GZiped size* is an average proof size with GZip compression
 * *Batched size* is an average proof size with batching compression
 * *Old apply time* is an average proof generation time without batching 
 * *New apply time* is an average proof generation time with batching
 * *Old verify time* is an average proof verification time without batching 
 * *New verify time* is an average proof verification time with batching


### Blockchain Processing Simulation Benchmark




