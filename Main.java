import javax.naming.directory.SearchResult;
import java.lang.module.FindException;
import java.util.*;

public class Main {
    final int MAX_ITERATIONS = 256;
    static String breakChars = "()^*/+-";

    static class searchResult {
        double num1;
        double num2;
        int indexFirst;
        int indexLast;
    }
    // recursive function used to solve equation
    static searchResult findNums(String equation, int index) {
        searchResult SR = new searchResult();
        StringBuilder buffer = new StringBuilder();
        // backwards
        for (int i = index-1; i >= 0; --i) {
            boolean valid = true;
            for (int j = 0; j < breakChars.length(); ++j) {
                if (equation.charAt(i) == breakChars.charAt(j)) {
                    valid = false;
                    break;
                }
            }
            if (!valid || i == 0) {
                if (valid && i == 0) {
                    buffer.insert(0,equation.charAt(i));
                }
                try {
                    SR.indexFirst = i;
                    //SR.indexLast = index;
                    SR.num1 = Double.parseDouble(buffer.toString());
                    break;
                }
                catch(Exception e) {
                    //  Block of code to handle errors
                }
            }
            buffer.insert(0,equation.charAt(i));
        }

        buffer.setLength(0); // clears stringBuilder
        // forwards
        for (int i = index+1; i < equation.length(); ++i) {
            boolean valid = true;
            for (int j = 0; j < breakChars.length(); ++j) {
                if (equation.charAt(i) == breakChars.charAt(j)) {
                    valid = false;
                    break;
                }
            }
            if (!valid || i == equation.length()-1) {
                if (valid && i == equation.length()-1) {
                    buffer.append(equation.charAt(i));
                    SR.indexLast = i+1;
                } else {
                    SR.indexLast = i;
                }
                try {
                    //SR.indexFirst = index;
                    SR.num2 =  Double.parseDouble(buffer.toString());
                    break;
                }
                catch(Exception e) {
                    //  Block of code to handle errors
                }
            }
            buffer.append(equation.charAt(i));
        }

        return SR;
    }

    public static class depthResult {
        int startIndex = 0;
        int endIndex = 0;
        int depth = 0;
        int additive = 0;
    };

    public static int temp = 0;
    // only scans forwards!
    static int add = 0;
    static int shortest = 0;
    static depthResult findBracketDepth(String equation) {
        depthResult dr = new depthResult();
        System.out.println("EQUATION (BR DEPTH): " + equation);
        //depthResult dr = new depthResult();
        int index = 0;
        for (int i = 0; i < equation.length(); ++i) {
            if (equation.charAt(i) == '(') {
                index = i;
                break;
            }
        }

        //depthResult[] drList = new depthResult[50];
        int bracketCountLeft = 0;
        int bracketCountRight = 0;
        dr.startIndex = index;
        dr.endIndex = index;
        int shift = 0;
        boolean deepest = false;
        boolean bracketFound = false;
        //while (!deepest) {

            for (int i = index; i < equation.length(); ++i) {
                if (equation.charAt(i) == '(') {
                    ++shift;
                    bracketFound = true;
                }
                if (equation.charAt(i) == ')') {
                    --shift;
                    if (shift == 0) {
                        dr.endIndex = i;
                        //equation = equation.substring(dr.startIndex+1,dr.endIndex);
                        ++dr.depth;
                    }
                    bracketFound = true;
                }
            }

       //}

        //System.out.println("(BR_DEPTH) EQUATION: " + equation);
        //System.out.println("SUBSTR: " + equation.substring(dr.startIndex+1,dr.endIndex-1));
        if (bracketFound && !(dr.endIndex == 0 && dr.startIndex == 0)) {
            dr.additive += dr.startIndex+1;
            add += dr.startIndex+1;
            shortest = dr.endIndex - dr.startIndex;
            //System.out.println("START: " + dr.startIndex);
            //System.out.println("END: " + dr.endIndex);
            //System.out.println("ADDITIVE: " + add);
            dr = findBracketDepth(equation.substring(dr.startIndex + 1, dr.endIndex));
            if ((dr.endIndex > dr.startIndex)) {
                equation = equation.substring(dr.startIndex + 1, dr.endIndex);
            }
        }
        return dr;
    }

    static String cutString(String str, int cutBegin, int cutEnd) {
        StringBuilder SB = new StringBuilder();
        for (int j = 0; j < str.length(); ++j) {
            if (j < cutBegin || j > cutEnd) {
                SB.append(str.charAt(j));
            }
        }
        return SB.toString();
    }

    static String replace(String str, String replaceWith, int replaceBegin, int replaceEnd) {
        StringBuilder SB = new StringBuilder();
        for (int j = 0; j < str.length(); ++j) {
            if (j < replaceBegin || j > replaceEnd) {
                SB.append(str.charAt(j));
            }
            if (j == replaceBegin) {
                SB.append(replaceWith);
            }
        }
        return SB.toString();
    }

    static String eval(String equation) {

        double result = 0;
        String breakChars2 = "()^*/+-";
        while (true) {
            System.out.println("EQUATION (EVAL): " + equation);

            boolean found = false;
            for (int i = 0; i < equation.length(); ++i) {
                for (int j = 0; j < breakChars2.length(); ++j) {
                    if (equation.charAt(i) == breakChars2.charAt(j)) {
                        found = true;
                    }
                }
            }
            if (!found) {
                break;
            }
            for (int i = 0; i < equation.length(); ++i) {
                if (equation.charAt(i) == '(') {
                    int depth1;
                    int depth2;
                    depthResult dr = findBracketDepth(equation);
                    String sub = equation.substring(add, add + shortest - 1);
                    System.out.println("SUBSTR: " + equation.substring(add, add + shortest - 1));
                    equation = replace(equation, eval(sub),add-1,add + shortest - 1);
                    System.out.println("NEW: " + equation);
                }
            }
            for (int i = 0; i < equation.length(); ++i) {
                if (equation.charAt(i) == '^') {
                    searchResult SR = findNums(equation, i);
                    double a = SR.num1;
                    double b = SR.num2;
                    result = Math.pow(a, b);
                    equation = replace(equation, Double.toString(result), SR.indexFirst, SR.indexLast - 1);
                    break;
                }
            }
            for (int i = 0; i < equation.length(); ++i) {
                if (equation.charAt(i) == '*') {
                    searchResult SR = findNums(equation, i);
                    double a = SR.num1;
                    double b = SR.num2;
                    result = a * b;
                    equation = replace(equation,Double.toString(result),SR.indexFirst, SR.indexLast-1);
                    break;
                }
                else if (equation.charAt(i) == '/') {
                    searchResult SR = findNums(equation, i);
                    double a = SR.num1;
                    double b = SR.num2;
                    result = a / b;
                    replace(equation, Double.toString(result), SR.indexFirst, SR.indexLast - 1);
                    break;
                }
            }
            for (int i = 0; i < equation.length(); ++i) {
                if (equation.charAt(i) == '+') {
                    searchResult SR = findNums(equation, i);
                    double a = SR.num1;
                    double b = SR.num2;
                    result = a + b;
                    System.out.println("RESULT: " + a + "+" + b + "=" + result);
                    equation = replace(equation, Double.toString(result), SR.indexFirst, SR.indexLast - 1);
                    System.out.println("EQUATION (EVAL): " + equation);
                    break;
                }
                else if (equation.charAt(i) == '-') {
                    searchResult SR = findNums(equation, i);
                    double a = SR.num1;
                    double b = SR.num2;
                    result = a - b;
                    equation = replace(equation,Double.toString(result),SR.indexFirst, SR.indexLast-1);
                    break;
                }
            }
        }
        return  equation;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter an equation: ");
        //String equation = input.nextLine();
        String equation = "5*(2+5)+6";
        double i = Double.parseDouble(eval(equation));
        System.out.println("The result of the equation is: " + i);
        return;
    }
}
