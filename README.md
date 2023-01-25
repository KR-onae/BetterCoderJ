# What? BetterCoderJ..? What is it?
BetterCoderJ is a program that helps you compile Java more easily, faster, and create JAR files more easily.
It is made in Java, and from v1.1.1, exe files are also available.

# Then... How to use?
USAGE: BetterCoderJ-1.1.0.exe [<OPTION>[-<VALUE>]]
Examples:
  # Compile all files in 'a' directory.
    BetterCoderJ-1.1.0.exe c-a
  # Compile all files in 'a' directory,
  # Compress the files in directory 'a' into 'abc.jar'.
    BetterCoderJ-1.1.0.exe i-a o-abc.jar c-a j
  # Run JAR archive file with name 'abc.jar'.
    BetterCoderJ-1.1.0.exe r-abc.jar
  # Compress the files in directory 'a' into 'abcd.jar',
  # And run the output file.
    BetterCoderJ-1.1.0.exe i-a o-abcd.jar j r-*o
  # Set JAVA_HOME folder location to %JAVA_HOME%.
    BetterCoderJ-1.1.0.exe jh -"%JAVA_HOME%"

Options:
  # jh-<loc>
    Set the location where Java is installed.
  #i-<dir>
    Set the input directory. This is used with the j option.
  #o-<jar>
    Sets the path of the JAR compressed file to output. This is used with the j option.
  # c-<dir>
    Compile all files inside a directory.
  #j
    Compresses all files in the directory set by the 'i' option to create a JAR archive in .
    The file is saved in the location set in the 'o' option.
  # r-<jar>
    Run the JAR archive.
