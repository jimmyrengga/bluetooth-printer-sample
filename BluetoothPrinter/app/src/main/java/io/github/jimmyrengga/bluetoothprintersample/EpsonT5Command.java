package io.github.jimmyrengga.bluetoothprintersample;

/**
 * Created by jimmy on 11/13/15.
 */
public class EpsonT5Command {

    /*
    **  BASIC CONTROL COMMANDS
     */
    public static final char ESC   = (char) 27;
    //Clear the data in the print buffer; Restore the default of each print command
    public static final char[] RESET   = {ESC, (char) 64};
    //awaken printer
    public static final char NULL  = (char) 0;
    //Print and feed paper to black mark position
    public static final char FF    = (char) 12;
    //Prints the data in the print buffer and feeds on line based on the current line spacing.
    public static final char LF    = (char) 10;
    //same as LF
    public static final char CR    = (char) 13;
    //Horizontal tab
    public static final char HT    = (char) 9;

    /*
    ** Character Setting Commands
     */
    // Select Underline
    public static final char[] UNDERLINE_ON    = {ESC, (char) 45, '1'};
    // cancel / clear underline
    public static final char[] UNDERLINE_OFF   = {ESC, (char) 45, '0'};
    // Select Bold
    public static final char[] BOLD_ON         = {ESC, (char) 69, '1'};
    // cancel / clear underline
    public static final char[] BOLD_OFF        = {ESC, (char) 69, '0'};
    // Select Overprinting
    public static final char[] OVERPRINTING_ON = {ESC, (char) 71, '1'};
    // cancel / clear Overprinting
    public static final char[] OVERPRINTING_OFF= {ESC, (char) 71, '0'};
    // Select Reverse Printing
    public static final char[] RESERVE_PRINTING_ON = {ESC, (char) 66, '1'};
    // cancel / clear Reverse Printing
    public static final char[] RESERVE_PRINTING_OFF= {ESC, (char) 66, '0'};

    /*
    ** Print Setting Commands
     */
    // Set to Default line spacing
    public static final char[] DEFAULT_LINE_SPACING = {ESC, (char) 50};
    // Aligment Left
    public static final char[] ALIGNMENT_LEFT = {ESC, (char) 97, '0'};
    // Aligment Middle
    public static final char[] ALIGNMENT_MIDDLE = {ESC, (char) 97, '1'};
    // Aligment Right
    public static final char[] ALIGNMENT_RIGHT = {ESC, (char) 97, '2'};



}
