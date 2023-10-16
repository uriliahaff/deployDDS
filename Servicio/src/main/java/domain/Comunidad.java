package domain;

import java.util.List;

public class Comunidad
{
    private List<Usuario> miembros;
    private double gradoDeConfianza;

    public List<Usuario> getMiembros() {
        return miembros;
    }

    public double getGradoDeConfianza() {
        return gradoDeConfianza;
    }

    public void setGradoDeConfianza(double gradoDeConfianza) {
        this.gradoDeConfianza = gradoDeConfianza;
    }
}
