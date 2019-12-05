package br.ce.wcaquino.servicos;


import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.*;

import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class LocacaoServiceTest {

    private LocacaoService service;
    private SPCService spcService;
    private LocacaoDAO locacaoDAO;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        service = new LocacaoService();
        locacaoDAO = Mockito.mock(LocacaoDAO.class);
        service.setLocacaoDAO(locacaoDAO);
        spcService = Mockito.mock(SPCService.class);
        service.setSpcService(spcService);
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        // error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        // error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
        error.checkThat(locacao.getDataLocacao(), ehHoje());
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void naoDeveAlugarFilmeSemEstoque() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        List<Filme> filmes = Collections.singletonList(new Filme("Filme 1", 0, 4.0));

        //acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
        //cenario
        List<Filme> filmes = Collections.singletonList(new Filme("Filme 1", 1, 5.0));

        //acao
        try {
            service.alugarFilme(null, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), is("Usuario vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");

        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        //acao
        service.alugarFilme(usuario, null);
    }

    @Test
    // @Ignore Ignora aquele test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
        // cenario
        Usuario usuario = new Usuario("Wender");
        List<Filme> filmes = Collections.singletonList(new Filme("Filme 1", 1, 5.0));

        Locacao retorno = service.alugarFilme(usuario, filmes);

        // boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
        // Assert.assertTrue(ehSegunda);
        assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws FilmeSemEstoqueException, LocadoraException {
        // Cenario
        Usuario usuario = new Usuario("Wender");
        Usuario usuario2 = new Usuario("Wender");
        List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));

        when(spcService.possuiNegativacao(usuario)).thenReturn(true);

        exception.expect(LocadoraException.class);
        exception.expectMessage("Usuário negativado");

        service.alugarFilme(usuario2, filmes);
    }
}
