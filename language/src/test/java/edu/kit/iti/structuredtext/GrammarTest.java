package edu.kit.iti.structuredtext;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import edu.kit.iti.structuredtext.antlr.StructuredTextLexer;
import edu.kit.iti.structuredtext.antlr.StructuredTextParser;

public class GrammarTest {

    @Test
    public void test() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = (Document) builder.build(
                getClass().getResourceAsStream("StructuredText_01.gunit.xml")
        );

        Element rootNode = document.getRootElement();

        for (Element parser : rootNode.getChildren("parser")) {
            String val_ml = parser.getAttributeValue("multiline");
            boolean multiline = Boolean.valueOf(val_ml);
            String rule = parser.getAttributeValue("rule");

            if (multiline) {
                test_line_parser(parser.getTextTrim(), rule);
            } else {
                for (String line : parser.getTextTrim().split("\n")) {
                    test_line_parser(line, rule);
                }
            }

        }
    }

    private void test_line_lexer(String tmp) {
        StructuredTextLexer stl = new StructuredTextLexer(new ANTLRInputStream(
                tmp));
        List<? extends Token> tokens = stl.getAllTokens();

        for (int i = 0; i < 2; i++) {
            for (Token token : tokens) {
                String text = token.getText();
                String type = StructuredTextLexer.tokenNames[token.getType()];
                int length = Math.max(text.length(), type.length());

                if (i == 0)
                    System.out.format(" %-" + length + "s ", text);
                else
                    System.out.format(" %-" + length + "s ", type);
            }
            System.out.println();
        }
    }




    private void test_line_parser(String tmp, String rule) throws Exception {
        System.out.println("==================================================");
        System.out.println(tmp);

        StructuredTextLexer stl = new StructuredTextLexer(new ANTLRInputStream(
                tmp));
        CommonTokenStream cts = new CommonTokenStream(stl);
        StructuredTextParser stp = new StructuredTextParser(cts);


        stp.setBuildParseTree(true);
        Class<?> clazz = stp.getClass();
        Method method = clazz.getMethod(rule);
        method.invoke(stp);
        if(stp.getNumberOfSyntaxErrors()!=0){
           MyTestRig mtr = new MyTestRig(tmp, rule);
        }
        System.out.flush();
        System.err.flush();
        System.out.println("==================================================");


    }
}
