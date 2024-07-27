package mz.co.fer.Outros;

/**
 *
 * @author Deockilion
 */
public class IDgerator {

    private static int prefixo = 0;
    private static int sufixoNum = 0;
    private static char sufixoLetra = 'A';

    public static String gerarIdRecibo(String prefixoo, char letra, String sufixo) {

        IDgerator.prefixo = Integer.parseInt(prefixoo);
        IDgerator.sufixoLetra = letra;
        IDgerator.sufixoNum = Integer.parseInt(sufixo);

        // Incrementar o sufixo numérico se for menor que 999
        if (sufixoNum < 999) {
            sufixoNum++;
        } else if (sufixoNum == 999) {   // Se atingir 999, reiniciar e incrementar o sufixo alfabético
            sufixoNum = 1;

            // Incrementar o prefixo se for menor que '99'
            if (prefixo < 99) {
                prefixo++;
            } else if (prefixo == 99) { // Se atingir '99', lançar uma exceção indicando que o limite foi atingido
                prefixo = 1;
                // Incrementar o sufixo alfabético se for menor que 'Z'
                if (letra != 'Z') {
                    letra++;
                } else {   // Se atingir 'Z', reiniciar e incrementar o prefixo
                    letra = 'A';
                }
            }
        }

        // Formatar o ID do recibo
        return String.format("%02d", prefixo) + letra + String.format("%03d", sufixoNum);

    }

//    public static void main(String[] args) {
//        IDgerator gerador = new IDgerator();
//        //String id = "99H888";
//        for (int i = 0; i < 2000; i++) { // Gerar 1100 IDs para demonstração
//            System.out.println(gerador.gerarIdRecibo("01", 'A', "001"));
//        }
//
//    }

}
