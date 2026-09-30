package com.tny.game.protobuf.format;
/* 
    Copyright (c) 2009, Orbitz World Wide
    All rights reserved.

    Redistribution and use in source and binary forms, with or without modification, 
    are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright notice, 
          this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright notice, 
          this list of conditions and the following disclaimer in the documentation 
          and/or other materials provided with the distribution.
        * Neither the name of the Orbitz World Wide nor the names of its contributors 
          may be used to endorse or promote products derived from this software 
          without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
    OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
    SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.*;

import java.io.IOException;
import java.util.*;

/**
 * Provide ascii html formatting support for proto2 instances.
 * <p>
 * (c) 2009-10 Orbitz World Wide. All Rights Reserved.
 *
 * @author eliran.bivas@gmail.com Eliran Bivas
 * @version $HtmlFormat.java Mar 12, 2009 4:00:33 PM$
 */
public final class Protobuf2HtmlFormat {

    private static final String META_CONTENT = "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />";
    private static final String MAIN_DIV_STYLE = "color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;";
    private static final String FIELD_NAME_STYLE = "font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;";
    private static final String FIELD_VALUE_STYLE = "color: #3300FF;font-size: 13px; font-family: sans-serif;";

    /**
     * Outputs a textual representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public static void print(Message message, Appendable output) throws IOException {
        HtmlGenerator generator = new HtmlGenerator(output);
        printTitle(message, generator);
        print(message, generator);
        generator.print("</body></html>");
    }

    private static void printTitle(final Message message, final HtmlGenerator generator) throws IOException {
        generator.print("<html><head>");
        generator.print(META_CONTENT);
        generator.print("<title>");
        generator.print(message.getDescriptorForType().getFullName());
        generator.print("</title></head><body>");
        generator.print("<div style=\"");
        generator.print(MAIN_DIV_STYLE);
        generator.print("\">message : ");
        generator.print(message.getDescriptorForType().getFullName());
        generator.print("</div>");
    }

    /**
     * Outputs a textual representation of {@code fields} to {@code output}.
     */
    public static void print(UnknownFieldSet fields, Appendable output) throws IOException {
        HtmlGenerator generator = new HtmlGenerator(output);
        generator.print("<html>");
        generator.print(META_CONTENT);
        generator.print("</head><body>");
        printUnknownFields(fields, generator);
        generator.print("</body></html>");
    }

    /**
     * Like {@code print()}, but writes directly to a {@code String} and returns it.
     */
    public static String printToString(Message message) {
        try {
            StringBuilder text = new StringBuilder();
            print(message, text);
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException("Writing to a StringBuilder threw an IOException (should never happen).",
                    e);
        }
    }

    /**
     * Like {@code print()}, but writes directly to a {@code String} and returns it.
     */
    public static String printToString(UnknownFieldSet fields) {
        try {
            StringBuilder text = new StringBuilder();
            print(fields, text);
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException("Writing to a StringBuilder threw an IOException (should never happen).",
                    e);
        }
    }

    private static void print(Message message, HtmlGenerator generator) throws IOException {

        for (Map.Entry<FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
            printField(field.getKey(), field.getValue(), generator);
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public static void printField(FieldDescriptor field, Object value, HtmlGenerator generator) throws IOException {

        if (field.isRepeated()) {
            // Repeated field. Print each element.
            for (Object element : (List<?>) value) {
                printSingleField(field, element, generator);
            }
        } else {
            printSingleField(field, value, generator);
        }
    }

    private static void printSingleField(FieldDescriptor field,
            Object value,
            HtmlGenerator generator) throws IOException {
        if (field.isExtension()) {
            generator.print("[<span style=\"");
            generator.print(FIELD_NAME_STYLE);
            generator.print("\">");
            // We special-case MessageSet elements for compatibility with proto1.
            if (field.getContainingType().getOptions().getMessageSetWireFormat()
                && (field.getType() == FieldDescriptor.Type.MESSAGE) && (field.isOptional())
                // object equality
                && (field.getExtensionScope() == field.getMessageType())) {
                generator.print(field.getMessageType().getFullName());
            } else {
                generator.print(field.getFullName());
            }
            generator.print("</span>]");
        } else {
            generator.print("<span style=\"");
            generator.print(FIELD_NAME_STYLE);
            generator.print("\">");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print("</span>");
        }

        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            generator.print(" <span style=\"color: red;\">{</span><br/>");
            generator.indent();
        } else {
            generator.print(": ");
        }

        printFieldValue(field, value, generator);

        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            generator.outdent();
            generator.print("<span style=\"color: red;\">}</span>");
        }
        generator.print("<br/>");
    }

    private static void printFieldValue(FieldDescriptor field, Object value, HtmlGenerator generator) throws IOException {
        generator.print("<span style=\"");
        generator.print(FIELD_VALUE_STYLE);
        generator.print("\">");
        switch (field.getType()) {
            case INT32:
            case INT64:
            case SINT32:
            case SINT64:
            case SFIXED32:
            case SFIXED64:
            case FLOAT:
            case DOUBLE:
            case BOOL:
                // Good old toString() does what we want for these types.
                generator.print(value.toString());
                break;

            case UINT32:
            case FIXED32:
                generator.print(FormatTextSupport.unsignedToString((Integer) value));
                break;

            case UINT64:
            case FIXED64:
                generator.print(FormatTextSupport.unsignedToString((Long) value));
                break;

            case STRING:
                generator.print("\"");
                generator.print(value.toString());
                generator.print("\"");
                break;

            case BYTES: {
                generator.print("\"");
                generator.print(FormatTextSupport.escapeBytesOctal((ByteString) value));
                generator.print("\"");
                break;
            }

            case ENUM: {
                generator.print(((EnumValueDescriptor) value).getName());
                break;
            }

            case MESSAGE:
            case GROUP:
                print((Message) value, generator);
                break;
        }
        generator.print("</span>");
    }

    private static void printUnknownFields(UnknownFieldSet unknownFields, HtmlGenerator generator) throws IOException {
        for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
            UnknownFieldSet.Field field = entry.getValue();

            for (long value : field.getVarintList()) {
                generator.print(entry.getKey().toString());
                generator.print(": ");
                generator.print(FormatTextSupport.unsignedToString(value));
                generator.print("<br/>");
            }
            for (int value : field.getFixed32List()) {
                generator.print(entry.getKey().toString());
                generator.print(": ");
                generator.print(String.format((Locale) null, "0x%08x", value));
                generator.print("<br/>");
            }
            for (long value : field.getFixed64List()) {
                generator.print(entry.getKey().toString());
                generator.print(": ");
                generator.print(String.format((Locale) null, "0x%016x", value));
                generator.print("<br/>");
            }
            for (ByteString value : field.getLengthDelimitedList()) {
                generator.print(entry.getKey().toString());
                generator.print(": \"");
                generator.print(FormatTextSupport.escapeBytesOctal(value));
                generator.print("\"<br/>");
            }
            for (UnknownFieldSet value : field.getGroupList()) {
                generator.print(entry.getKey().toString());
                generator.print(" <span style=\"color: red;\">{</span><br/>");
                generator.indent();
                printUnknownFields(value, generator);
                generator.outdent();
                generator.print("<span style=\"color: red;\">}</span><br/>");
            }
        }
    }


    /**
     * An inner class for writing text to the output stream.
     */
    static private final class HtmlGenerator {

        Appendable output;
        boolean atStartOfLine = true;

        public HtmlGenerator(Appendable output) {
            this.output = output;
        }

        /**
         * Indent text by two spaces. After calling Indent(), two spaces will be inserted at the
         * beginning of each line of text. Indent() may be called multiple times to produce deeper
         * indents.
         *
         * @throws IOException
         */
        public void indent() throws IOException {
            print("<div style=\"margin-left: 25px\">");
        }

        /**
         * Reduces the current indent level by two spaces, or crashes if the indent level is zero.
         *
         * @throws IOException
         */
        public void outdent() throws IOException {
            print("</div>");
        }

        /**
         * Print text to the output stream.
         */
        public void print(CharSequence text) throws IOException {
            int size = text.length();
            int pos = 0;

            for (int i = 0; i < size; i++) {
                if (text.charAt(i) == '\n') {
                    write("<br/>", i - pos + 1);
                    pos = i + 1;
                    this.atStartOfLine = true;
                }
            }
            write(text.subSequence(pos, size), size - pos);
        }

        private void write(CharSequence data, int size) throws IOException {
            if (size == 0) {
                return;
            }
            if (this.atStartOfLine) {
                this.atStartOfLine = false;
            }
            this.output.append(data);
        }
    }

}
