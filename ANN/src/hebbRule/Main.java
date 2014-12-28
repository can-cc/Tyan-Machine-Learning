package hebbRule;
public class Main {
            /**
            * @param args the command line arguments
            */
            public static void main(String[] args) {
            // TODO code application logic here
            Hebb h = new Hebb();
            h.recognize(1, 1);
            h.recognize(1, 0);
            h.recognize(0, 1);
            h.recognize(0,0);
            }

            }
