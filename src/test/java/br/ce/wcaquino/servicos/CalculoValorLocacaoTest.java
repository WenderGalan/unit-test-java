package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Data
@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    @InjectMocks
    private LocacaoService service;
    @Mock
    private LocacaoDAO locacaoDAO;
    @Mock
    private SPCService spcService;

    @Parameterized.Parameter
    public List<Filme> filmes;

    @Parameterized.Parameter(value = 1)
    public Double valorLocacao;

    @Parameterized.Parameter(value = 2)
    public String cenario;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private static Filme filme1 = Filme.builder().nome("Filme 1").estoque(2).precoLocacao(4.0).build();
    private static Filme filme2 = Filme.builder().nome("Filme 2").estoque(2).precoLocacao(4.0).build();
    private static Filme filme3 = Filme.builder().nome("Filme 3").estoque(2).precoLocacao(4.0).build();
    private static Filme filme4 = Filme.builder().nome("Filme 4").estoque(2).precoLocacao(4.0).build();
    private static Filme filme5 = Filme.builder().nome("Filme 5").estoque(2).precoLocacao(4.0).build();
    private static Filme filme6 = Filme.builder().nome("Filme 6").estoque(2).precoLocacao(4.0).build();
    private static Filme filme7 = Filme.builder().nome("Filme 7").estoque(2).precoLocacao(4.0).build();

    // @Parameterized.Parameters(name = "Teste {index} = {0} - {1}")
    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getParametros() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList(filme1, filme2), 8.0, "2 Filmes: Sem Desconto"},
                {Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25%"},
                {Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 75%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 Filmes: 100%"},
                {Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 Filmes: Sem Desconto"}
        });
    }

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
        // cenario
        Usuario usuario = Usuario.builder().nome("Wender").build();

        // acao
        Locacao resultado = service.alugarFilme(usuario, filmes);

        // verificacao
        // 4 + 4 + 3 + 2 + 1 = 14
        assertThat(resultado.getValor(), is(valorLocacao));
    }

    @Test
    public void print() {
        System.out.println(valorLocacao);
    }
}
