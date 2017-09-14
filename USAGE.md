# jdjitools
Java DJI Tools

## Short usage help

### Overall info

First of all, you need to run jdjitools console. Of course you need installed Java (JRE) on your system, or tool will not work.
On Windows system you simply need to start jdjitools.exe form "bin" folder and console window should appear.
On Mac you need to run Terminal, navigate to "bin" folder and execute jdjitools.sh (you may need to make it executable firstly). And same on Linux.

When you will launch the tool, it will display a simple command-line interface (CLI). You'll see  
**jdt> _**  
prompt if everything is ok.

Now you need to type a command and press return to do something. Like in Windows Command Prompt, Linux shell or Mac Terminal. The commands consists of "system" ones and "user" ones. You can list all commands by typing **?list-all** or its abbreviation (btw, all commands have abbreviations) **?la**. If you want to list only "user" commands, you can type **?list** or **?l**.

"System" commands intended to some "housekeeping" things: run a script, enable or disable logging session to file, listing commands, displaying commands help and even generating HTML commands help file. Most useful system command is **?help** or **?h**. If you type **?h some-command** it will display detailed info about that command, list it parameters, comments, help, etc.

"User" commands will do actually all the useful things. Lets analyze them one by one:

### About

Long form: **about**  
Short form: **ab**  

Will display information about jDJItools.

### Utility: DJI derive key

Long form: **util-dji-derive-key**  
Short form: **uddk**  

This is the rewrite of DJI's key derivation algo, that can be used, for example, to make your personal DAAK (debug activation key) or other useful things, especially if you tamper with internals, firmware and rooting.
So, as in original DJI algo, you need to supply source key, source parameter and (optional) resulting key length:  
**uddk 00112233445566778899AABBCCDDEEFF test 8**  
will generate resulting key, derived form 00112233445566778899AABBCCDDEEFF key and "test" parameter, cutting result down to 8 bytes.  
**uddk 00112233445566778899AABBCCDDEEFF test 32**  
will do same thing but will display full 32-bytes resulting key.  
**uddk 00112233445566778899AABBCCDDEEFF test**  
will generate a resulting key with default length of 16 bytes.

### Utility: Decrypt DJI FTP file

Long form: **util-decrypt-ftp-file**  
Short form: **udff**  

Files downloaded from some DJI devices embedded FTP servers is encrypted with AES and scrampling. This utility will decrypt such a file.
You can use one of two known keys at this moment: 1 (old) or 2 (new):  
**udff c:\encrypted.log 1**  
will decrypt "c:\encrypted.log" with key 1 and REWRITE its contents.  
**udff c:\encrypted.log c:\decrypted.log 2**  
will decrypt that file with key 2 and write decrypted contents to the "c:\decrypted.log" file.

### Serial: List serial ports

Long form: **serial-list-serial-ports**  
Short form: **slsp**  

Will list available serial ports in system that you can use (f.e. with DUML utilities).

### DUML: Monitor serial port

Long form: **duml-monitor-serial-port**  
Short form: **dmsp**  

Will open serial port and start listening and displaying (decoding) all incoming DUML traffic. It can be useful for debugging or tinkering with your DJI equipment. You can also save all decoded DUML messages to file log instead of displaying it to console.  
**dmsp com1**  
will start listening for DUML traffic on COM1 port and display decoded DUML messages to console.  
**dmsp com1 c:\duml.log**  
will do the same thing but will save decoded DUML messages to the "c:\duml.log" file.

### DUML: Decode binary file

Long form: **duml-decode-binary-file**  
Short form: **ddbf**  

Will decode all DUML messages one by one found in a file. It can be useful if you have sniffed some DUML session to a binary file and want to parse it. You can also save all decoded DUML messages to file log instead of displaying it to console.  
**ddbf c:\sniffed.bin**  
will parse "c:\sniffed.bin" file and display all found DUML messages to console.  
**ddbf c:\sniffed.bin c:\sniffed.log**  
will do the same thing but will save decoded DUML messages to the "c:\sniffed.log" file.

### DUML: Send arbitrary DUML message

Long form: **duml-send-arbitrary-message**  
Short form: **dsam**  

Will send any DUML message you want to the serial port. You need to understand what is DUML and how it works, of course. It also can wait for response message if you will specify that you want response (ACK). For example:  
**dsam com1 0x2A 0x28 0x1234 true 0x00 0x01**  
will send DUML message from PC address (0x2A) to Aircraft Linux (0x28) address, sequence number will be 0x1234, "true" means that you want response back from Aircraft Linux, command set is 0x00 and command is 0x01 ("Get version").
You can specify all parameters as hexadecimal (starting from "0x") or decimal numbers for your convenience.
Also you can append data to your message ("Get version" in previous example was data-less command) as last parameter(s). You can specify as many data parameters as you want. Data parameters can be hexadecimal (starting from "0x") or if there is no "0x" it will be parsed as ASCII text (if you have spaces in your text, you need to isolate it in double quotes):  
**dsam com1 0x2A 0x28 0x1234 true 0x00 0x01 0x1234 test**  
will send "Get version" command, but will append additional data to the message (of course, that data will be ignored by Aircraft Linux). Resulting data of "0x1234 test" parameters will be send as 0x123474657374.
