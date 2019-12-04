import br.ce.wcaquino.entidades.Usuario;
import org.junit.Assert;
import org.junit.Test;

public class AssertTest {

    @Test
    public void test() {
        // Recebe um valor boleano
        Assert.assertTrue(true);
        Assert.assertFalse(false);

        // Verifica se os valores são iguais
        Assert.assertEquals("Erro de comparação", 1, 1);
        // Para o double e floar precisa de um delta de margem de erro de comparação
        Assert.assertEquals(0.51234, 0.512, 0.001);
        Assert.assertEquals(Math.PI, 3.14, 0.01);

        // Autoboxing e unboxing não existe para o Assert.equals
        int i  = 5;
        Integer i2 = 5;
        Assert.assertEquals(Integer.valueOf(i), i2);

        // Comparação com string
        Assert.assertEquals("bola", "bola");
        Assert.assertNotEquals("bola", "bola2");

        // Comaparação de objetos
        Usuario u1 = new Usuario("Usuario 1");
        Usuario u2 = new Usuario("Usuario 1");
        // Objetos comparados pelo equals da classe
        Assert.assertEquals(u1, u2);
        // Objetos da mesma instancia
        Assert.assertSame(u2, u2);



    }
}
