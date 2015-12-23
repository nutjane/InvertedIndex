# Inverted Index
Inverted Index and Boolean Query Processing and Optimization

#### 1. GOAL & IMPORTANT NOTE

The goal of this project is to develop a Java program that (1) takes a text document collection into account
and creates as well as manipulates an inverted index and (2) performs a Boolean query processing and
optimization.

#### 2. INVERTED INDEX

Conceptually, the inverted index comprises a dictionary and a posting. The dictionary consists of a list of
words in an alphabetical order. Each word is associated with a posting which is a list of document ID containing
the word.

It is required that the deliverable program must be able to dump the inverted index into a text file. In the
text file, each line represents an index entry which is alphabetically ordered. Each index entry has the following
format:

> t,df: < docID,docID, … >

where t is a term, df is a document frequency (i.e., the number of documents containing the term t), and
<docID,docID, … > is a list of document identifiers containing the term t. Note that the delimiter between df and
the list of documents must be ':' (colon), and between t and df as well as among docIDs in the list must be ','
(comma).

For example, given collection of documents as follows:

doc1: Daddy loves cats.

doc2: Let’s go!

doc3: Sister loves dogs

The inverted index text file must be the following:

cats,1:<1>

daddy,1:<1>

dogs,1:<3>

go,1:<2>

let,1:<2>

loves,2:<1,3>

s,1:<2>

sister,1:<3>

#### 3. BOOLEAN QUERIES AND QUERY OPTIMIZATION

Conceptually, a Boolean query is a Boolean expression which consists of terms connecting with Boolean
operations and it can be nested. In this project, the symbols: '&', '|' and '~' are used to represent the Boolean operations: AND, OR and NOT, respectively. The parentheses i.e., rounded brackets '(' and ')' will be used for
nested expressions. 

#### 4. TERM EXTRACTION

The term extraction (i.e., text processing) is the first process of the inverted index construction as well as the
query processing. In this project, the term extraction includes only the tokenization, the case-folding and the stop
word elimination; token normalization and stemming/lemmatization are not included.

The tokenization performs as follows: Each token is separated by a whitespace including newline character
(\n), tab character (\t), and all punctuation marks. The case-folding converts string into lower case, e.g. from “Daddy” to “daddy”, from “BANGKOK to
“Bangkok”. For the stop word elimination, a list of stop words is provided.

#### 5. PROGRAM FEATURE AND INTERFACE

A command line interface is required to interact with users. The program supports the following
commands: create, update, save, and search. Details of each command can be described as follows:

##### 5.1 Create

FORMAT: `create <index> from <FILES>`

DESCRIPTIONS: Create a new inverted index for a collection of documents. If such index exists, the
program must remove it and create a new index. Any kind of data structure can be
used as the index structure. The <index> is the name of index file. The <FILES> is a
list of names of text files each of which represents a document in the collection. The
names are separated by a whitespace. File extensions (.txt) need not be specified.

EXAMPLE: `create index from doc1 doc2 doc3`

##### 5.2 Update

FORMAT: `update <index> from <FILES>`

DESCRIPTIONS: Incrementally update the inverted index with respect to a set of documents. The
<index> is the name of index file that wants to be updated. The <FILES> is a list of
names of text files each of which represents a document.

EXAMPLE: `update index from doc2 doc3`

##### 5.3 Save

FORMAT: `save <index>`

DESCRIPTIONS: Dump the index as text on screen and stored it as a text file whose formatted is
described in Section 2. The <index> is the name of index file. Note that this command
is normally used after create or update.

Example: `save index`

##### 5.4 Search

FORMAT: `search [Boolean_Query]`

DESCRIPTIONS: Find all documents which are satisfied a given Boolean_Query whose syntax is
described in Section 3. Note that the query must bounded by the square brackets '[' and
']'. The return results are shown as a list of document IDs each of which is shown in one
line.

EXAMPLE: `search [cats & dogs]`

OUTPUT:
`doc1
doc3`

