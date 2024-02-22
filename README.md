# Cubecobra Printer Java Version

Create a pdf from a MTG cube from cubecobra.com

It creates two PDFs of one-sided cards and two-sided cards of high quality images from scryfall.com and adds borders for optimized printing.

Script can be run interactively by givining no arguments (recommended).

Example execution script from compiled jar file.
`java -jar ./target/cubecobra-printer-1.0-jar-with-dependencies.jar`

Alternatively a cubecobra cube url can be given as an argument for directly download (only use if you know what you are doing). This also accepts an optional second argument to create multiple chunks of the final pdf of the chunk size set in argument. Don't provide this second argument if you wan't the cube in a full pdf.

Example execution script from compiled jar file with the two optional arguments:

`java -jar ./target/cubecobra-printer-1.0-jar-with-dependencies.jar https://cubecobra.com/cube/overview/simple-test 30`
