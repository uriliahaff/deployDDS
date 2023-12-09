package sever;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class App {

        public static EntityManagerFactory entityManagerFactory;
        public static void main(String[] args) {
            Map<String, String> env = System.getenv();
            for (String string : env.keySet()) {
                System.out.println(string + ": " + env.get(string));
            }


            Server.init();
        }

}