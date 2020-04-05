### Python exceptions parser

The implemented parser is able to extract information from the python exceptions.
The program receives a message of the following form:
```
Traceback (most recent call last):
  <Stack trase>
             ^
<Error name>: <error message>
```

The \<Stack trase> consists of the following types of elements: 
```
File "<file path>", line <line number>, <additional info>
    <annotation>
```

The output of the program is an instance of **PythonExceptionImpl** class, containing location, type, message and stack trase of the given python exception.

### Tests
<ul>
    <li> 
        <b>Given test</b> 
    </li>
    <li> 
        <b>Standard tests</b> - tests generated with the standard error types, file paths, and error messages
     </li>
    <li> 
        <b>Random tests</b> - tests with the randomly generated error types, file paths, line numbers, annotations, length of stack trase, and error messages 
    </li>
    <li>
        <b>Wrong input format tests</b> - tests to check whether the parser is able to throw exceptions when given the input of the wrong format.
    </li>
</ul>
