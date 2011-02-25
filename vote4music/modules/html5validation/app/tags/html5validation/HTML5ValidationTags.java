/* Project: play-html5-validation
 * Package: tags.html5validation
 * File   : HTML5ValidationTags
 * Created: Dec 5, 2010 - 7:27:42 PM
 *
 *
 * Copyright 2010 Sebastian Hoß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package tags.html5validation;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import play.Play;
import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.Range;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.Model;
import play.exceptions.TemplateCompilationException;
import play.mvc.Scope.RenderArgs;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;

/**
 * <h1>Overview</h1>
 * <p>The HTML5 validation tags provide a simple <code>#{input /}</code> tag which can be used as a drop-in
 * replacement for existing HTML5 <code>&lt;input&gt;</code> elements.</p>
 *
 * <p>The <code>#{input /}</code> will try to map existing data validation annotations from your Play! model
 * to the HTML5 input element and thus provide near codeless client-side validation without using JavaScript.</p>
 *
 * <p>On top of that it supports all available attributes from the original HTML5 input element and auto-
 * fills the <code>name</code> attribute by default.</p>
 *
 * <p>For that to work you have to specify the model instance and its field you want to map by using the
 * <em>for</em> attribute:<br>
 * <br>
 * <code>#{input for:'user.name' /}</code></p><br>
 * 
 * <p>In addition to the {@link #STANDARD_ATTRIBUTES standard attributes} this tags supports the following extra
 * attributes:
 *  <ul>
 *      <li><strong>for</strong>: Used to specify the model instance and the models field.</li>
 *      <li><strong>attributes</strong>: Used to declare additional attributes</li>
 *  </ul>
 * </p>
 *
 * <h1>Caveats</h1>
 * <ul>
 *  <li>The MinSize validator can not be mapped to any HTML5 attribute currently.</li>
 *  <li>Contrary to HTML5 input elements the <code>#{input /}</code> tag must be properly closed.</li>
 *  <li>Currently it is possible to define each attribute multiple times since this class does not
 *  check or sanitize the input.</li>
 * </ul>
 *
 * <h1>Examples</h1>
 * <ol>
 *  <li>
 *      <p>Username validation</p>
 *      <p>Suppose you have a {@link Model} called <code>User</code> which has a field called
 *      <code>name</code> declared as</p><br>
 *
 *      <p><code>@Required<br>
 *      @MaxSize(8)<br>
 *      public String name;</code></p><br>
 *
 *      <p>and you pass an instance of that class called <em>user</em> around, you then can
 *      specify the field from that instance inside the <code>#{input /}</code> as follows:</p>
 *
 *      <p><code>#{input for:'user.name', id:'YourID', class:'class1 clas2' /}</code></p><br>
 *
 *      <p>The tag will then output the following HTML code:</p><br>
 *
 *      <p><code>&lt;input name="user.name" value="${user?.name}" id="YourID" class="class1 class2" required
 *      maxlength="8"&gt;</code></p><br>
 *  </li>
 *  <li>
 *      <p>Using additional attributes</p>
 *      <p>When you want to give the final <code>&lt;input&gt;</code> some additional attributes like
 *      <code>data-validate</code> you use the <em>attributes</em> attribute from the <code>#{input /}</code>
 *      tag:</p><br>
 * 
 *      <p><code>#{input for:'user.name', attributes:'data-validate="..."' /}</code></p><br>
 * 
 *      <p>This produces the following HTML code:</p><br>
 * 
 *      <p><code>&lt;input name="user.name" value="${user?.name}" data-validate="..."&gt;</code></p>
 *  </li>
 * </ol>
 *
 * <h1>How to help</h1>
 * <ul>
 *  <li>Test the tag and write back about errors, bugs and wishes.</li>
 * </ul>
 *
 * @author  Sebastian Hoß (mail@shoss.de)
 * @version 1.0
 * @see     <a href="http://www.w3.org/TR/html-markup/input.html">HTML5 Input Element</a>
 * @see     <a href="http://www.playframework.org/documentation/1.1/validation-builtin">Built-in
  				validations by Play!</a>
 * @see     <a href="http://diveintohtml5.org/forms.html">Mark Pilgrim on HTML5 Forms</a>
 */
@SuppressWarnings("nls")
public final class HTML5ValidationTags extends FastTags {

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    // *                                            CONSTANTS                                            *
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /** A list of standard attributes which can be found on HTML5 <code>&lt;input&gt;</code> elements. */
    public static final List<String> STANDARD_ATTRIBUTES = Arrays.asList("type", "id", "class", "form",
            "placeholder", "list", "step", "dir", "draggable", "hidden", "accesskey", "contenteditable",
            "contextmenu", "lang", "spellcheck", "style", "tabindex", "title", "disabled", "autocomplete",
            "autofocus", "checked", "value");

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    // *                                             METHODS                                             *
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * <p>Generates an HTML5 <code>&lt;input&gt;</code> element with some values derived from a given field.</p>
     * 
     * @param args      The tag attributes.
     * @param body      The tag body.
     * @param out       The print writer to use.
     * @param template  The parent template.
     * @param fromLine  The current execution line.
     */
    public static void _input(final Map<?, ?> args, final Closure body, final PrintWriter out,
            final ExecutableTemplate template, final int fromLine) {
        try {
            // Open input tag
            out.print("<input");

            // Print standard attributes
            printStandardAttributes(args, out);

            // Print validation attributes
            printValidationAttributes(args, out);

            // Print additional attributes
            printAdditionalAttributes(args, out);

            // Close input tag
            out.println(">");

        } catch (final SecurityException exception) {
            // TODO: Instead of null pass template.template
            throw new TemplateCompilationException(null, Integer.valueOf(fromLine), exception.getMessage());
        } catch (final NoSuchFieldException exception) {
            throw new TemplateCompilationException(null, Integer.valueOf(fromLine), exception.getMessage());
        } catch (final IllegalArgumentException exception) {
            throw new TemplateCompilationException(null, Integer.valueOf(fromLine), exception.getMessage());
        } catch (final ClassNotFoundException exception) {
            throw new TemplateCompilationException(null, Integer.valueOf(fromLine), exception.getMessage());
        }
    }

    /**
     * <p>Print all standard attributes which have to be specified by the user itself.</p>
     *
     * @param args  The map containing the wanted attributes.
     * @param out   The print writer to use.
     */
    private static void printStandardAttributes(final Map<?, ?> args, final PrintWriter out) {
        for (final String attribute : STANDARD_ATTRIBUTES) {
            if (args.containsKey(attribute) && (args.get(attribute) != null)) {
                printAttribute(attribute, args.get(attribute).toString(), out);
            }
        }
    }

    /**
     * <p>Prints validation attributes for a given field.</p>
     * 
     * @param args                          The tag attributes.
     * @param out                           The print writer to use.
     * @throws SecurityException            Thrown when either the field or the getter for the field can't be reached.
     * @throws NoSuchFieldException         Thrown when the field can't be reached.
     * @throws ClassNotFoundException 		Thrown when the class could not be found.
     */
    private static void printValidationAttributes(final Map<?, ?> args, final PrintWriter out) throws ClassNotFoundException,
    SecurityException, NoSuchFieldException {
        final String fieldname = args.get("for").toString();
        final String[] components = fieldname.split("\\.");

        Class<?> clazz = null;

        for (final Class<?> current : Play.classloader.getAllClasses()) {
            if (current.getSimpleName().equalsIgnoreCase(components[0])) {
                clazz = current;
            }
        }

        final Field field = clazz.getField(components[1]);

        // Print the name of the field
        printAttribute("name", fieldname, out);

        // Print the value of the field
        final Object object = RenderArgs.current().get(components[0], clazz);
        if (object != null){
            try {
                try {
                    // Try to use the getter if any exists
                    final Method getter = clazz.getMethod("get" + JavaExtensions.capFirst(field.getName()));

                    // Print the value returned by the getter
                    printAttribute("value", getter.invoke(object), out);
                } catch (final NoSuchMethodException  e) {
                    // No getter exists

                    // Print the current value of the field inside the current object
                    printAttribute("value", field.get(object), out);
                }
            } catch (final IllegalAccessException exception) {
                // print nothing
            } catch (final InvocationTargetException exception) {
                // print nothing
            }
        }

        // Mark readonly
        if (Modifier.isFinal(field.getModifiers()) || args.containsKey("readonly")) {
            printAttribute("readonly", "readonly", out);
        }

        // Print the validation data
        if (field.isAnnotationPresent(Required.class)) {
            printAttribute("required", "required", out);
        }

        if (field.isAnnotationPresent(Min.class)) {
            final Min min = field.getAnnotation(Min.class);
            printAttribute("min", String.valueOf(min.value()), out);
        }

        if (field.isAnnotationPresent(Max.class)) {
            final Max max = field.getAnnotation(Max.class);
            printAttribute("max", String.valueOf(max.value()), out);
        }

        if (field.isAnnotationPresent(Range.class)) {
            final Range range = field.getAnnotation(Range.class);
            printAttribute("min", String.valueOf(range.min()), out);
            printAttribute("max", String.valueOf(range.max()), out);
        }

        if (field.isAnnotationPresent(MaxSize.class)) {
            final MaxSize maxSize = field.getAnnotation(MaxSize.class);
            printAttribute("maxlength", String.valueOf(maxSize.value()), out);
        }

        if (field.isAnnotationPresent(Match.class)) {
            final Match match = field.getAnnotation(Match.class);
            printAttribute("pattern", match.value(), out);
        }

        if (field.isAnnotationPresent(URL.class)) {
            printAttribute("type", "url", out);
        }

        if (field.isAnnotationPresent(Email.class)) {
            printAttribute("type", "email", out);
        }
    }

    /**
     * <p>Prints additional attributes passed in through the <code>attributes</code> attribute.</p>
     * 
     * @param args	The tag attributes.
     * @param out	The print writer to use.
     */
    private static void printAdditionalAttributes(final Map<?, ?> args, final PrintWriter out) {
        if (args.containsKey("attributes")) {
            out.print(" " + args.get("attributes"));
        }
    }

    /**
     * <p>Prints a single attribute using a given print writer.</p>
     * 
     * <p>If <code>null</code> is given as value nothing will be printed to eliminate empty attributes.</p>
     *
     * @param name      The name of the attribute to print.
     * @param value     The value of the attribute to print.
     * @param out       The print writer to use.
     */
    private static void printAttribute(final String name, final Object value, final PrintWriter out) {
        if (value != null) {
            out.print(" " + name + "=\"" + value + "\"");
        }
    }

}