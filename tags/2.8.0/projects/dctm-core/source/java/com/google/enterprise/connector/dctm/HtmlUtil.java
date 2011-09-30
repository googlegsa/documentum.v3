// Copyright 2006 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.connector.dctm;

/** HTML form construction utilities. */
class HtmlUtil {
  public static final String HIDDEN = "hidden";

  public static final String VALUE = "value";

  public static final String NAME = "name";

  public static final String TEXT = "text";

  public static final String TYPE = "type";

  public static final String INPUT = "input";

  public static final String CLOSE_ELEMENT = "/>";

  public static final String OPEN_ELEMENT = "<";

  public static final String TBODY_START = "<tbody";

  public static final String TBODY_END = "</tbody>\r\n";

  public static final String TR_END = "</tr>\r\n";

  public static final String TD_END = "</td>\r\n";

  public static final String TD_START = "<td>";

  public static final String TD_START_LABEL =
      "<td style='white-space: nowrap'>";

  public static final String TD_START_TEXTAREA =
      "<td style='white-space: nowrap; vertical-align: top; padding-top: 2px'>";

  public static final String TD_START_PADDING =
      "<td style='white-space: nowrap; padding-top: 1ex'>\r\n";

  public static final String TD_START_COLSPAN = "<td colspan='2'>";

  public static final String TD_START_CENTER =
      "<td style='text-align: center'>";

  public static final String TR_START = "<tr>\r\n";

  public static final String TR_START_HIDDEN =
      "<tr style='display: none'>\r\n";

  public static final String SELECT_START = "<select";

  public static final String SELECT_END = "</select>\r\n";

  public static final String SELECTED = " selected='selected'";

  public static final String TEXTAREA_START = "<textarea";

  public static final String TEXTAREA_END = "</textarea>\r\n";

  public static final String SCRIPT_START =
      "<script type=\"text/javascript\"><![CDATA[\n";

  public static final String SCRIPT_END = "]]></script>\n";

  public static final String PASSWORD = "password";

  public static final String CHECKBOX = "checkbox";

  public static final String CHECKED = " checked='checked'";

  public static final String ID = "id";

  public static void appendInput(StringBuilder buf, String name, String value,
      boolean isPassword) {
    buf.append(OPEN_ELEMENT);
    buf.append(INPUT);
    if (isPassword) {
      appendAttribute(buf, TYPE, PASSWORD);
    } else {
      appendAttribute(buf, TYPE, TEXT);
    }
    appendAttribute(buf, VALUE, value);
    appendAttribute(buf, NAME, name);
    appendAttribute(buf, ID, name);
    buf.append(CLOSE_ELEMENT);
  }

  public static void appendCheckbox(StringBuilder buf, String name,
      boolean isOn, String onClick) {
    buf.append(OPEN_ELEMENT);
    buf.append(INPUT);
    appendAttribute(buf, TYPE, CHECKBOX);
    appendAttribute(buf, VALUE, "on"); // Also the browsers default value.
    appendAttribute(buf, NAME, name);
    appendAttribute(buf, ID, name);

    if (onClick != null) {
      appendAttribute(buf, "onclick", onClick);
    }

    if (isOn) {
      buf.append(CHECKED);
    }
    buf.append(CLOSE_ELEMENT);
  }

  public static void appendLabel(StringBuilder buf, String name, String label) {
    buf.append("<label");
    appendAttribute(buf, "for", name);
    buf.append(">");
    buf.append(label);
    buf.append("</label>");
  }

  public static void appendTextarea(StringBuilder buf, String name,
      String value) {
    buf.append(TEXTAREA_START);
    appendAttribute(buf, NAME, name);
    appendAttribute(buf, "rows", "10");
    appendAttribute(buf, "cols", "50");
    buf.append(">");
    escapeAndAppend(buf, value);
    buf.append(TEXTAREA_END);
    appendEndRow(buf);
  }

  public static void appendOption(StringBuilder buf, String value,
      String text) {
    appendOption(buf, value, text, false);
  }

  public static void appendOption(StringBuilder buf, String value, String text,
      boolean isSelected) {
    buf.append("<option");
    appendAttribute(buf, "value", value);
    if (isSelected) {
      buf.append(SELECTED);
    }
    buf.append(">");
    buf.append(text);
    buf.append("</option>\n");
  }

  public static void appendStartRow(StringBuilder buf, String key,
      boolean isRequired) {
    buf.append(TR_START);
    buf.append(TD_START_LABEL);
    buf.append(key);
    if (isRequired) {
      buf.append("<span style='color: red; font-weight: bold; ")
          .append("margin: auto 0.3em;\'>*</span>");
    }
    buf.append(TD_END);
    buf.append(TD_START);
  }

  public static void appendStartTextareaRow(StringBuilder buf, String key) {
    buf.append(TR_START);
    buf.append(TD_START_TEXTAREA);
    buf.append(key);
    buf.append(TD_END);
    buf.append(TD_START);
  }

  public static void appendStartHiddenRow(StringBuilder buf) {
    buf.append(TR_START_HIDDEN);
    buf.append(TD_START);
    buf.append(TD_END);
    buf.append(TD_START);
  }

  public static void appendStartTable(StringBuilder buf) {
    buf.append(TR_START);
    buf.append(TD_START_COLSPAN);
    buf.append("<table style=\"width: 100%; border-collapse: collapse\">");
  }

  public static void appendStartTbody(StringBuilder buf, String id,
      String display) {
    buf.append(TBODY_START);
    appendAttribute(buf, ID, id);
    if (display != null) {
      appendAttribute(buf, "style", "display: " + display);
    }
    buf.append(">\r\n");
  }

  public static void appendEndTbody(StringBuilder buf) {
    buf.append(TBODY_END);
  }

  public static void appendStartTableRow(StringBuilder buf, String leftKey,
      String rightKey) {
    buf.append(TR_START);
    buf.append(TD_START_PADDING);
    buf.append(leftKey);
    buf.append(TD_END);
    buf.append(TD_START);
    buf.append("&nbsp;");
    buf.append(TD_END);
    buf.append(TD_START_PADDING);
    buf.append(rightKey);
    buf.append(TD_END);
    buf.append(TR_END);
    buf.append(TR_START);
    buf.append(TD_START);
  }

  public static void appendEndTable(StringBuilder buf) {
    buf.append("</table>");
    appendEndRow(buf);
  }

  public static void appendEndRow(StringBuilder buf) {
    buf.append(TD_END);
    buf.append(TR_END);
  }

  public static void appendHiddenInput(StringBuilder buf, String name,
      String value) {
    appendHiddenInput(buf, null, name, value);
  }

  public static void appendHiddenInput(StringBuilder buf, String id,
      String name, String value) {
    appendStartHiddenRow(buf);
    buf.append("<input type=\"hidden\"");
    if (id != null) {
      appendAttribute(buf, ID, id);
    }
    appendAttribute(buf, "name", name);
    appendAttribute(buf, "value", value);
    buf.append(" />");
    appendEndRow(buf);
  }

  public static void appendSelectStart(StringBuilder buf, String name) {
    appendSelectStart(buf, null, name);
  }

  public static void appendSelectStart(StringBuilder buf, String id,
      String name) {
    buf.append(SELECT_START);
    appendAttribute(buf, NAME, name);
    if (id != null) {
      appendAttribute(buf, ID, id);
    }
    buf.append(" style=\"width:100%\" multiple='multiple' size=\"10\">\n");
  }

  public static void appendAttribute(StringBuilder buf, String attrName,
      String attrValue) {
    buf.append(" ");
    buf.append(attrName);
    buf.append("=\"");
    escapeAndAppendAttributeValue(buf, attrValue);
    buf.append("\"");
    if (attrName == TYPE && (attrValue == TEXT || attrValue == PASSWORD)) {
      buf.append(" size=\"50\"");
    }
  }

  /**
   * Escapes the given attribute value and appends it.
   *
   * @see #escapeAndAppend
   * @see <a href="http://www.w3.org/TR/REC-xml/#syntax"
   * >http://www.w3.org/TR/REC-xml/#syntax</a>
   */
  /* TODO: Replace with XmlUtils or its successor. */
  /* Copied from com.google.enterprise.connector.otex.LivelinkConnectorType. */
  private static void escapeAndAppendAttributeValue(StringBuilder buffer,
      String data) {
    for (int i = 0; i < data.length(); i++) {
      char c = data.charAt(i);
      switch (c) {
        case '\'':
          // Preferred over &apos; see http://www.w3.org/TR/xhtml1/#C_16
          buffer.append("&#39;");
          break;
        case '"':
          buffer.append("&quot;");
          break;
        case '&':
          buffer.append("&amp;");
          break;
        case '<':
          buffer.append("&lt;");
          break;
        case '\t':
        case '\n':
        case '\r':
          buffer.append(c);
          break;
        default:
          if (c >= 0x20 && c <= 0xFFFD) {
            buffer.append(c);
          }
          break;
      }
    }
  }

  /**
   * Escapes the given character data and appends it.
   *
   * @see #escapeAndAppendAttributeValue
   * @see <a href="http://www.w3.org/TR/REC-xml/#syntax"
   * >http://www.w3.org/TR/REC-xml/#syntax</a>
   */
  /* TODO: Replace with XmlUtils (new method) or its successor. */
  /* Copied from com.google.enterprise.connector.otex.LivelinkConnectorType. */
  private static void escapeAndAppend(StringBuilder buffer, String data) {
    for (int i = 0; i < data.length(); i++) {
      char c = data.charAt(i);
      switch (c) {
        case '&':
          buffer.append("&amp;");
          break;
        case '<':
          buffer.append("&lt;");
          break;
        case '\t':
        case '\n':
        case '\r':
          buffer.append(c);
          break;
        default:
          if (c >= 0x20 && c <= 0xFFFD) {
            buffer.append(c);
          }
          break;
      }
    }
  }

  public static void appendStartOnclickButton(StringBuilder buf, String label) {
    buf.append("<input");
    appendAttribute(buf, "type", "button");
    appendAttribute(buf, "value", label);
    buf.append(" onclick=\"");
  }

  public static void appendEndOnclickButton(StringBuilder buf) {
    // Putting whitepace between the close input tag and the br tag
    // causes a misalignment in Chrome. The extra break doesn't seem
    // to cause vertical alignment issues in Chrome, Safari, Firefox,
    // or IE.
    buf.append("\"></input><br />\n");
  }
}
