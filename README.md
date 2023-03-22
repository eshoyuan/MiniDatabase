# Task 2
The logic for extracting join conditions from the body of a query can be found in the JoinOperator constructor, located in the code provided. The specific section of the code that implements this logic starts at the beginning of the constructor and continues until the end of the first loop.

## Code Explanation
The JoinOperator constructor takes two arguments, dbPath (the database path) and query (the Query object). The code processes the query to extract join conditions, selection conditions, and relational atoms.

The code iterates through each Atom object in the query's body. If an Atom is an instance of ComparisonAtom, it checks if both terms in the comparison are instances of Variable. If they are, it adds the ComparisonAtom to the joinAtoms list, as it represents a join condition. If not, it adds the ComparisonAtom to the selectionAtoms list, as it represents a selection condition.

If an Atom is an instance of RelationalAtom, it adds the atom to the relationalAtoms list.

After processing the query's body, the code then handles the case where there are implicit conditions in the RelationalAtom. It checks if a variable has already appeared in the query. If it has, it creates a new variable with a random name and adds a new join condition using this new variable. It then updates the selection conditions and the RelationalAtom with the new variable.

# Task 3

## Optimization Rules for JoinOperator

### Rule 1: Transforming the query
Before executing the join operation, the query is transformed to ensure that there are no constants and variables with the same name. This step helps in simplifying the join conditions and reducing the possibility of incorrect join results.

### Rule 3: Renaming variables
The code renames the variables that appear multiple times in the query. This helps in eliminating ambiguity and ensures that the join conditions are applied correctly during the query evaluation process.

### Rule 2: Pushing down selection operators
The code pushes down the selection operators to the leaf nodes of the join tree. By applying the selection operators early on in the query evaluation process, we can effectively reduce the size of intermediate results. This is because the selection operators filter out irrelevant tuples based on the specified conditions, leaving only the relevant tuples to be used in the subsequent join operations. This optimization not only reduces the memory usage but also helps in speeding up the query execution process, as fewer tuples need to be considered during the join operations.

## Optimization Rules for SumOperator

### Rule 1: Use of HashMap for storing and summing the groups
The outputGroup HashMap is used to store the sum of each group of tuples. When a new tuple is retrieved, it checks whether the group already exists in the outputGroup HashMap. If it does, the sum for that group is updated; otherwise, a new entry is added to the HashMap. This approach minimizes the storage of intermediate tuples and directly computes the sum, reducing memory usage.

### Rule 2: ProjectOperator or JoinOperator based on the number of RelationAtoms
The SumOperator constructor counts the number of RelationAtoms in the query. If there is only one RelationAtom, the ProjectOperator is used, while the JoinOperator is used for queries with more than one RelationAtom. This rule ensures that the most efficient operator is chosen based on the query's structure, which reduces the size of intermediate results and improves performance.

### Rule 3: Avoiding storage of all tuples in a list
The outputAfterSum list is used to store the final results of the sum operation. Instead of storing all intermediate tuples in a list and performing the sum operation at the end, the SumOperator calculates the sum on-the-fly, as explained in Rule 1. This approach reduces memory consumption and optimizes the computation process.

In summary, these optimization rules are correct because they allow the SumOperator to compute the sum aggregate function efficiently by minimizing the storage of intermediate tuples and directly computing the sum. This approach reduces memory usage and improves query evaluation performance.

