package us.norskog.knime.json.simpleparser;

import java.util.HashMap;
import java.util.Map;

/**
 * fast scanner for flat json
 * no scientific notation
 *
 */
public class SimpleParserCode 
{
	enum State {START, LQ_WS_RB, KEY, KEY_ESCAPED, COLON_WS, VALUE_WS, STRING, STRING_ESCAPED, 
		INTEGER, DOUBLE, NUMBER_POINT_WS, COMMA_WS_RB, END};

		Map<String, Object> scan(String json) {
			Map<String, Object> things = new HashMap<String, Object>();
			StringBuilder sb = new StringBuilder();
			State state = State.START;
			String key = null;
			boolean isDouble = false;
			System.out.println("'" + json + "'");
			System.out.print("'");
			for(char ch: json.toCharArray()) {
				System.out.print(ch);
				switch(state) {
				case START:
					if (ch == '{' || ws(ch)) {
						state = State.LQ_WS_RB;
					} else {
						fail(state, ch);
					}
					break;
				case LQ_WS_RB:
					if (ws(ch)) {
						;
					} else if (ch == '"') {
						state = State.KEY;
					} else if (ch == '}') {
						state = State.END;
					} else {
						fail(state, ch);
					}	
					break;
				case KEY:
					if (ch == '"') {
						state = State.COLON_WS;
						key = sb.toString();
						sb.setLength(0);
					} else if (ch == '\\') {
						state = State.KEY_ESCAPED;
					} else {
						sb.append(ch); 
					}
					break;
				case KEY_ESCAPED:
					sb.append(ch);
					state = State.KEY;
					break;
				case COLON_WS:
					if (ws(ch)) {
						;
					} else if (ch != ':') {
						fail(state, ch);
					} else {
						state = State.VALUE_WS;
					}
					break;
				case VALUE_WS:
					if (ws(ch)) {
						;
					} else if (ch == '"') {
						state = State.STRING;
					} else if (ch >= '0' && ch <= '9') {
						sb.append(ch);
						state = State.NUMBER_POINT_WS;
					} else {
						fail(state, ch);
					}
					break;
				case STRING:
					if (ch == '\\') {
						state = State.STRING_ESCAPED;
					} else if (ch == '"') {
						addString(things, key, sb.toString());
						sb.setLength(0);
						state = State.COMMA_WS_RB;
					} else {
						sb.append(ch);
					}
					break;
				case STRING_ESCAPED:
					sb.append(ch);
					state = State.STRING;
					break;
				case NUMBER_POINT_WS:
					if (ch >= '0' && ch <= '9') {
						sb.append(ch);
					} else if (ch == '.' || ch == 'e' || ch == 'E') {
						sb.append(ch);
						isDouble = true;
					} else if (ch == '+' || ch == '-') {
						sb.append(ch);
					} else if (ch == ',' || ch == '}' || ws(ch)) {
						addNumber(things, key, sb.toString(), isDouble);
						sb.setLength(0);
						isDouble = false;
						state = (ch == ',') ? State.LQ_WS_RB : ((ch == '}') ? state.END : State.COMMA_WS_RB);
					} else {
						fail(state, ch);
					}
					break;
				case COMMA_WS_RB:
					if (ws(ch)) {
						;
					} else if (ch == ',') {
						state = State.LQ_WS_RB;
					} else if (ch == '}') {
						state = State.END;
					} else {
						fail(state, ch);
					}
					break;
				case END:
					fail(state, ch);
					break;
				default:
					System.out.println("'");
					System.err.println("FAIL: state = " + state.toString());
					fail(state, ch);
				}
			}
			System.out.println("'");
			if (state != state.END) {
				fail(state, (char)-1);
			}
			return things;
		}

		private void fail(State state, char ch) {
			System.out.println("'");
			System.err.println("State: " + state.toString());
			System.err.println("char: '" + ch + "'");
		}

		void addNumber(Map<String, Object> things, String key, String numberValue, boolean isDouble) {
			try {
				if (isDouble) {
					Double x = Double.parseDouble(numberValue);
					things.put(key, x);
				} else {
					Long l = Long.parseLong(numberValue);
					things.put(key, l);
				}
			} catch (NumberFormatException e) {
				System.out.println("'");
				System.err.println("Number fail: " + key + " = " + numberValue + ", isDouble = " + isDouble);
			}
		}

		void addString(Map<String, Object> things, String key, String stringValue) {
			things.put(key, stringValue);
		}

		boolean ws(char ch) {
			return ch == ' ' || ch == '\t';
		}


		public static void main( String[] args )
		{
			SimpleParserCode app = new SimpleParserCode();
//			System.out.println( app.scan("{\"color\":\"red\", \"kind\":\"gravenstein\", \"products\":[\"juice\"], \"misc\":{\"country\":\"us\"}}") );
			System.out.println( app.scan("{\"color\":\"red\", \"amount\":1, \"weight\":0.3, \"sci\":1e5}") );
			System.out.println( app.scan("{\"color\":\"red\"}") );
			System.out.println( app.scan("{\"amount\":1}") );
			System.out.println( app.scan("{\"sci\":1e5}") );
			System.out.println( app.scan("{\"amount\":1, \"weight\":0.3, \"sci\":1e5}") );
			System.out.println( app.scan("{\"weight\":0.3}") );
			System.out.println( app.scan("{\"color\":\"red\", \"amount\":1, \"weight\":0.3, \"sci\":1e5}") );
		}
}