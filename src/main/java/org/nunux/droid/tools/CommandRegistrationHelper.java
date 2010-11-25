package org.nunux.droid.tools;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.nunux.droid.command.common.Command;
import org.nunux.droid.service.DroidService;

/**
 * Command registration helper.
 * @author Nicolas Carlier
 */
public class CommandRegistrationHelper {
    List<Command> commands;

    /**
     * Constroctor.
     * @param service Droid service
     * @param classes commands
     */
    public CommandRegistrationHelper(DroidService service, Class... classes) {
        commands = new ArrayList<Command>();
        for (Class clazz : classes) {
            Constructor constructor;
            try {
                constructor = clazz.getConstructor(new Class[]{DroidService.class});
            } catch (NoSuchMethodException ex) {
                Log.e(DroidService.TAG, "Unable to call command constructor: " + clazz.getName(), ex);
                continue;
            } catch (SecurityException ex) {
                Log.e(DroidService.TAG, "Unauthorise to call command constructor: " + clazz.getName(), ex);
                continue;
            }

            try {
                Command command = (Command) constructor.newInstance(service);
                commands.add(command);
                Log.d(DroidService.TAG, "Command found: " + command.getHelp());
            } catch (InstantiationException ex) {
                Log.e(DroidService.TAG, "Unable to instantiate command: " + clazz.getName(), ex);
            } catch (IllegalAccessException ex) {
                Log.e(DroidService.TAG, "Unable to instantiate command: " + clazz.getName(), ex);
            } catch (IllegalArgumentException ex) {
                Log.e(DroidService.TAG, "Unable to instantiate command: " + clazz.getName(), ex);
            } catch (InvocationTargetException ex) {
               Log.e(DroidService.TAG, "Unable to instantiate command: " + clazz.getName(), ex);
            }
        }
    }

    /**
     * FIXME NOT WORK!
     * @param packageName
     * @param service
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public CommandRegistrationHelper(String packageName, DroidService service) throws ClassNotFoundException, IOException {
        Log.d(DroidService.TAG, "CommandRegister. Check package: " + packageName);
        commands = new ArrayList<Command>();

        List<Class> commandClasses = new ArrayList<Class>();
        Class[] classes = CommandRegistrationHelper.getClasses(packageName, service.getClassLoader());
        for (Class clazz : classes) {
            Log.d(DroidService.TAG, "CommandRegister. Check class: " + clazz.getName() +
                    " extends " + clazz.getSuperclass().getName());
            if (clazz.getSuperclass().getName().equals(Command.class.getName())) {
                commandClasses.add(clazz);
            }
        }

        for (Class commandClass : commandClasses) {
            Constructor constructor;
            try {
                constructor = commandClass.getConstructor(new Class[]{DroidService.class});
            } catch (NoSuchMethodException ex) {
                Log.e(DroidService.TAG, "Unable to call command constructor: " + commandClass.getName(), ex);
                continue;
            } catch (SecurityException ex) {
                Log.e(DroidService.TAG, "Unauthorise to call command constructor: " + commandClass.getName(), ex);
                continue;
            }

            try {
                Command command = (Command) constructor.newInstance(service);
                commands.add(command);
                Log.d(DroidService.TAG, "Command found: " + command.getHelp());
            } catch (InstantiationException ex) {
                Log.e(DroidService.TAG, "Unable to instantiate command: " + commandClass.getName(), ex);
            } catch (IllegalAccessException ex) {
                Log.e(DroidService.TAG, "Unable to instantiate command: " + commandClass.getName(), ex);
            } catch (IllegalArgumentException ex) {
                Log.e(DroidService.TAG, "Unable to instantiate command: " + commandClass.getName(), ex);
            } catch (InvocationTargetException ex) {
               Log.e(DroidService.TAG, "Unable to instantiate command: " + commandClass.getName(), ex);
            }
        }
    }

    public final List<Command> getCommands() {
        return commands;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName, ClassLoader classLoader)
            throws ClassNotFoundException, IOException {
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        Log.d(DroidService.TAG, "CommandRegister. Founded ressources: " + dirs.size());
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                Log.d(DroidService.TAG, "CommandRegister. Founded class: " + file.getName());
            }
        }
        return classes;
    }
}
