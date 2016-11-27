/**
 * Created by storo_000 on 03.11.2014.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

class TR {
	public String s;

	public LexAn.lexType lt;

	TR(String a, LexAn.lexType l) {
		s = a;
		lt = l;
	}
}

class LexAn {
	private enum CT {ctWhite, ctLatin, ctOperator, ctPunct, ctDigit, ctSlash, ctBackslash, ctSharp, ctQuotes, ctApost, ctOther, ctEoln, ctEof, ctNone}

	;

	private char c;

	private CT ct;

	private BufferedReader reader;

	private HashSet<String> op = new HashSet<String>(
			Arrays.asList("-=", "+=", "++", "--", "==", "!=", ">=", "<=", "&&", "||", "<<", ">>", "*=", "/=", "%=",
					"&=", "|=", "^=", "<<=", ">>=", "->", ".*", "->*", "::"));

	private HashSet<String> reserved = new HashSet<String>(
			Arrays.asList("alignas", "alignof", "and", "and_eq", "asm", "auto", "bitand", "bitor", "bool", "break",
					"case", "catch", "char16_t", "char32_t", "class", "compl", "const", "constexpr", "const_cast",
					"continue", "decltype", "default", "delete", "do", "double", "dynamic_cast", "else", "enum",
					"explicit", "export", "extern", "false", "float", "for", "friend", "goto", "if", "inline", "int",
					"long", "mutable", "namespace", "new", "noexcept", "not", "not_eq", "nullptr", "operator", "or",
					"or_eq", "private", "protected", "public", "register", "reinterpret_cast", "return", "short",
					"signed", "sizeof", "static", "static_accert", "static_cast", "struct", "switch", "template",
					"this", "thread_local", "throw", "true", "try", "typedef", "typeid", "typename", "union",
					"unsigned", "using", "virtual", "void", "volatile", "wchar_t", "while", "xor", "xor_eq"));

	private boolean isOperator(char c) {
		if (c == '.' || c == '+' || c == '-' || c == '=' || c == '*' || c == '%' || c == '!' || c == '>' || c == '<' ||
				c == '&' || c == '|' || c == '^' || c == '*' || c == '-' || c == '[' || c == ']' || c == '?' || c == ':'
				|| c == '~' ||
				c == '(' || c == ')')
			return true;
		return false;
	}

	private CT readchar() throws Exception {
		int ch;
		if ((ch = reader.read()) != -1) {
			c = (char) ch;
			if (Character.isDigit(c))
				ct = CT.ctDigit;
			else if (ch == 10 || ch == 13)
				ct = CT.ctEoln;
			else if (c <= ' ')
				ct = CT.ctWhite;
			else if (Character.isLetter(c) || c == '_')
				ct = CT.ctLatin;
			else if (isOperator(c))
				ct = CT.ctOperator;
			else if (c == ',' || c == '{' || c == '}' || c == ';')
				ct = CT.ctPunct;
			else if (c == '#')
				ct = CT.ctSharp;
			else if (c == '/')
				ct = CT.ctSlash;
			else if (c == '\"')
				ct = CT.ctQuotes;
			else if (c == '\'')
				ct = CT.ctApost;
			else if (c == '\\')
				ct = CT.ctBackslash;
			else
				ct = CT.ctOther;
		} else {
			ct = CT.ctEof;
		}
		return ct;
	}

	;

	public enum lexType {Error, Eof, Eoln, Whites, Number, Number_8, Number_16, Const, Directive, Comment, Reserved, Operator, Punctuation, Identifier}

	;

	private lexType state2lexem(int state) {
		switch (state) {
		case 0:
			return lexType.Error;
		case 1:
			return lexType.Whites;
		case 2:
			return lexType.Directive;
		case 3:
			return lexType.Identifier;
		case 4:
			return lexType.Number;
		case 5:
			return lexType.Operator;
		case 6:
			return lexType.Punctuation;
		case 8:
			return lexType.Number;
		case 11:
			return lexType.Const;
		case 14:
			return lexType.Reserved;
		case 17:
			return lexType.Number_8;
		case 18:
			return lexType.Number_16;
		case 20:
			return lexType.Comment;
		case 23:
			return lexType.Comment;
		case 25:
			return lexType.Number;
		case 9:
			return lexType.Eoln;
		case 10:
			return lexType.Eof;
		default:
			return lexType.Error;
		}
	}

	;

	private int step(int state) {
		/*  0   initial
            1   read whites
            2   read line(directive)
            3   read latin|digit
            4   read digit
            5   read operator
            6   punct
            7   read const
            8   123.
            9   eoln
            10  eof
            11  endofstring
            12  ' read char
            13  'char read '
            14  "asd\
            15  '\
            16  0
            17  03(read0..7)
            18  0X(read0..f)
            19  /
            20  //
            21  /*
            22  /*asdafsdafdadad*
            23  endofcomment
            24  123.2E
            25  124.01E+
            100 eoln
            101 eof
            -1  error       */
		switch (state) {
		case 0:
			switch (ct) {
			case ctWhite: {
				state = 1;
				break;
			}
			case ctEoln: {
				state = 9;
				break;
			}
			case ctEof: {
				state = 10;
				break;
			}
			case ctQuotes: {
				state = 7;
				break;
			}
			case ctApost: {
				state = 12;
				break;
			}
			case ctDigit:
				if (c == '0') {
					state = 16;
					break;
				} else {
					state = 4;
					break;
				}
			case ctSharp: {
				state = 2;
				break;
			}
			case ctSlash: {
				state = 19;
				break;
			}
			case ctLatin: {
				state = 3;
				break;
			}
			case ctOperator: {
				state = 5;
				break;
			}
			case ctPunct: {
				state = 6;
				break;
			}
			default: {
				state = -1;
				break;
			}
			}
			break;
		case 1:
			if (ct == CT.ctWhite) {
				state = 1;
				break;
			} else {
				state = -1;
				break;
			}
		case 3:
			if (ct == CT.ctLatin) {
				state = 3;
				break;
			} else if (ct == CT.ctDigit) {
				state = 3;
				break;
			} else {
				state = -1;
				break;
			}
		case 4:
			if (ct == CT.ctDigit) {
				state = 4;
				break;
			} else if (c == 'e' || c == 'E') {
				state = 24;
				break;
			} else if (c == '.') {
				state = 8;
				break;
			} else {
				state = -1;
				break;
			}
		case 5: {
			if (ct == CT.ctOperator) {
				state = 5;
				break;
			} else {
				state = -1;
				break;
			}
		}
		case 7:
			if (ct == CT.ctQuotes) {
				state = 11;
				break;
			} else if (ct == CT.ctBackslash) {
				state = 14;
				break;
			} else if (ct != CT.ctEof) {
				state = 7;
				break;
			} else {
				state = -1;
				break;
			}
		case 8:
			if (ct == CT.ctDigit) {
				state = 8;
				break;
			} else if (c == 'e' || c == 'E') {
				state = 24;
				break;
			} else {
				state = -1;
				break;
			}
		case 11: {
			state = -1;
			break;
		}
		case 12:
			if (ct == CT.ctBackslash) {
				state = 15;
				break;
			} else if (ct != CT.ctEof && ct != CT.ctQuotes && ct != CT.ctApost) {
				state = 13;
				break;
			} else {
				state = -1;
				break;
			}
		case 13:
			if (ct == CT.ctApost) {
				state = 11;
				break;
			} else {
				state = -1;
				break;
			}
		case 14:
			if (ct == CT.ctQuotes || ct == CT.ctBackslash || ct == CT.ctApost) {
				state = 7;
				break;
			} else {
				state = -1;
				break;
			}
		case 15:
			if (ct == CT.ctQuotes || ct == CT.ctBackslash || ct == CT.ctApost || c == 'n' || c == 't' || c == 'b'
					|| c == 'f' || c == 'r') {
				state = 13;
				break;
			} else {
				state = -1;
				break;
			}
		case 16:
			if (ct == CT.ctDigit && c != '8' && c != '9') {
				state = 17;
				break;
			} else if (c == '.') {
				state = 8;
				break;
			} else if (c == 'x' || c == 'X') {
				state = 18;
				break;
			} else if (ct == CT.ctWhite || ct == CT.ctEoln || ct == CT.ctOperator || ct == CT.ctPunct
					|| ct == CT.ctEof) {
				state = 4;
				break;
			} else {
				state = -1;
				break;
			}
		case 17:
			if (c == '0' || (c >= '1' && c <= '7')) {
				state = 17;
				break;
			} else {
				state = -1;
				break;
			}
		case 18:
			if (ct == CT.ctDigit || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
				state = 18;
				break;
			} else {
				state = -1;
				break;
			}
		case 19:
			if (ct == CT.ctSlash) {
				state = 20;
				break;
			} else if (c == '*') {
				state = 21;
				break;
			} else if (ct == CT.ctWhite || ct == CT.ctEoln || ct == CT.ctOperator || ct == CT.ctPunct
					|| ct == CT.ctEof) {
				state = 5;
				break;
			} else {
				state = -1;
				break;
			}
		case 21:
			if (c == '*') {
				state = 22;
				break;
			} else if (ct != CT.ctEof) {
				state = 21;
				break;
			} else {
				state = -1;
				break;
			}
		case 22:
			if (ct == CT.ctSlash) {
				state = 23;
				break;
			} else if (ct != CT.ctEof) {
				state = 21;
				break;
			} else {
				state = -1;
				break;
			}
		case 23: {
			state = -1;
			break;
		}
		case 24:
			if (c == '+' || c == '-') {
				state = 25;
				break;
			} else if (ct == CT.ctDigit) {
				state = 25;
				break;
			} else {
				state = -1;
				break;
			}
		case 25:
			if (ct == CT.ctDigit) {
				state = 25;
				break;
			} else {
				state = -1;
				break;
			}
		case 10:
			if (ct == CT.ctEoln) {
				state = 10;
				break;
			} else {
				state = -1;
				break;
			}
		default:
			state = -1;
		}
		return state;
	}

	public LexAn(String fname) throws Exception {
		reader = null;
		ct = CT.ctNone;
		if (fname.isEmpty()) {
			return;
		} else {
			FileInputStream fis = new FileInputStream(fname);
			InputStreamReader isr = new InputStreamReader(fis);
			reader = new BufferedReader(isr);
		}
		readchar();
	}

	public TR getLex() throws Exception {
		String sLexem = "";
		int state = 0;
		int prevState = state;
		state = step(state);
		while (state >= 0) {
			if (state == 2) {
				while (ct != CT.ctEoln && ct != CT.ctEof) {
					sLexem = sLexem + c;
					readchar();
				}
				prevState = 2;
				break;
			}
			if (state == 20) {
				while (ct != CT.ctEoln && ct != CT.ctEof) {
					sLexem = sLexem + c;
					readchar();
				}
				prevState = 20;
				break;
			}
			if (ct != CT.ctEof && ct != CT.ctWhite && ct != CT.ctEoln)
				if ((prevState == 5) && (state == 5)) {
					String s1 = sLexem + c;
					if (op.contains(s1))
						sLexem = sLexem + c;
					else
						break;
				} else
					sLexem = sLexem + c;
			if ((state == 7 && ct == CT.ctWhite) || (state == 1 && ct == CT.ctWhite) || (state == 21 && (
					ct == CT.ctWhite || ct == CT.ctEoln)))
				sLexem = sLexem + c;
			readchar();
			prevState = state;
			state = step(state);
		}
		if (reserved.contains(sLexem))
			prevState = 14;
		if ((ct == CT.ctLatin) && prevState == 4) {
			return new TR("", lexType.Error);
		} else
			return new TR(sLexem, state2lexem(prevState));
	}
}

public class Solution {
	public static void main(String[] args) throws Exception {
		String n = "C:\\Users\\storo_000\\IdeaProjects\\Lab3\\v0.2\\src\\input.txt";
		LexAn x = new LexAn(n);
		TR res = x.getLex();
		while (res.lt != LexAn.lexType.Error && res.lt != LexAn.lexType.Eof) {
			if (res.lt != LexAn.lexType.Eoln && res.lt != LexAn.lexType.Whites)
				System.out.println("<" + res.s + ", " + res.lt + ">");
			res = x.getLex();
		}
		System.out.println(res.lt);
	}
}