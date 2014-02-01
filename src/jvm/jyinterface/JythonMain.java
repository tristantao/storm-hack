package jyinterface;

import jyinterface.factory.JythonFactory;
import jyinterface.interfaces.ClassifierType;

import org.python.util.PythonInterpreter;


public class JythonMain {

	ClassifierType cT;

    /** Creates a new instance of Main */
    public JythonMain() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JythonFactory jf = JythonFactory.getInstance();
        ClassifierType classifier = (ClassifierType) jf.getJythonObject(
                               "jyinterface.interfaces.ClassifierType", "classify.py");
        System.out.println("Classify Result: " + classifier.identify("My name is Tristan!"));
    }
}