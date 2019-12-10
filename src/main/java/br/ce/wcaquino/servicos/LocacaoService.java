package br.ce.wcaquino.servicos;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

public class LocacaoService {

    private LocacaoDAO locacaoDAO;
    private SPCService spcService;
    private EmailService emailService;

    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {
        if (usuario == null)
            throw new LocadoraException("Usuario vazio");

        if (filmes == null || filmes.isEmpty())
            throw new LocadoraException("Filme vazio");

        for (Filme filme : filmes) {
            if (filme.getEstoque() == 0)
                throw new FilmeSemEstoqueException();
        }

        boolean negativado;
        try {
            negativado = spcService.possuiNegativacao(usuario);
        } catch (Exception e) {
            throw new LocadoraException("Problemas com o SPC, tente novamente mais tarde.");
        }

        if (negativado)
            throw new LocadoraException("Usuário negativado");

        Locacao locacao = Locacao.builder().filmes(filmes).usuario(usuario).dataLocacao(new Date()).build();
        Double valorTotal = 0d;
        for (int i = 0; i < filmes.size(); i++) {
            Filme filme = filmes.get(i);
            Double valorFilme = filme.getPrecoLocacao();
            switch (i) {
                case 2:
                    valorFilme = valorFilme * 0.75;
                    break;
                case 3:
                    valorFilme = valorFilme * 0.50;
                    break;
                case 4:
                    valorFilme = valorFilme * 0.25;
                    break;
                case 5:
                    valorFilme = 0.0;
                    break;
            }
            valorTotal += valorFilme;
        }
        locacao.setValor(valorTotal);

        //Entrega no dia seguinte
        Date dataEntrega = new Date();
        dataEntrega = adicionarDias(dataEntrega, 1);
        if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY))
            dataEntrega = adicionarDias(dataEntrega, 1);
        locacao.setDataRetorno(dataEntrega);

        //Salvando a locacao...
        locacaoDAO.salvar(locacao);

        return locacao;
    }

    public void notificarAtrasos() {
        List<Locacao> locacoes = locacaoDAO.obterLocacoesPendentes();
        for (Locacao locacao : locacoes) {
            if (locacao.getDataRetorno().before(new Date()))
                emailService.notificarAtraso(locacao.getUsuario());
        }
    }

    public void prorrogarLocacao(Locacao locacao, int dias) {
        Locacao novaLocacao = Locacao.builder()
                .usuario(locacao.getUsuario())
                .filmes(locacao.getFilmes())
                .dataLocacao(new Date())
                .dataRetorno(obterDataComDiferencaDias(dias))
                .valor(locacao.getValor() * dias)
                .build();
        locacaoDAO.salvar(novaLocacao);
    }
}