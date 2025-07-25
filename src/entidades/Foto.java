package entidades;

public class Foto {
    private int id;
    private String direccionFoto;

    public Foto() {}

    public Foto(int id, String direccionFoto) {
        this.id = id;
        this.direccionFoto = direccionFoto;
    }

    public int getId() {
        return id;
    }

    public String getDireccionFoto() {
        return direccionFoto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDireccionFoto(String direccionFoto) {
        this.direccionFoto = direccionFoto;
    }
}
