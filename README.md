This is a java version of Chinese tokenization descried in BERT, including basic tokenization and wordpiece tokenization.

## Motivation

In production, we usually deploy the BERT related model by tensorflow serving for high performance and flexibility. However, our application may not developed by python. Hence, we have to rewrite the tokenization module.

## Usage

Just run `Demo.java`, you can get result. Now, it support single and pair sentence both.

Moreover, for Chinese natural language processing, we add **full turn to half angle** and **uppercase to lowercase** operation.

## Reporting issues

Please let me know, if you encounter any problems.

