import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLException;

public class VentanaVet extends JFrame {

    private static class EdadInvalidaException extends Exception {
        public EdadInvalidaException(String message) {
            super(message);
        }
    }

    public VentanaVet() {
        setTitle("Gestion Clínica Veterinaria - Samuel Gómez");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 700);
        setLayout(new BorderLayout(10, 10));

        // Formulario (Superior)
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        String[] etiquetas = { "Nombre Mascota:", "Especie:", "Edad:", "Nombre Propietario:", "DNI Propietario:",
                "Accion:" };
        JTextField txtNom = new JTextField(15);
        JTextField txtEspecie = new JTextField(15);
        JTextField txtEdat = new JTextField(15);
        JTextField txtPropietari = new JTextField(15);
        JTextField txtDNI = new JTextField(15);
        JTextField[] campos = { txtNom, txtEspecie, txtEdat, txtPropietari, txtDNI };

        for (int i = 0; i < etiquetas.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panelFormulario.add(new JLabel(etiquetas[i]), gbc);

            gbc.gridx = 1;
            if (i < campos.length) {
                panelFormulario.add(campos[i], gbc);
            } else {
                JButton btnRegistrar = new JButton("Registrar Mascota");
                panelFormulario.add(btnRegistrar, gbc);

                // Boton  registrar mascota
                btnRegistrar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            int edadValidada = Integer.parseInt(txtEdat.getText().trim());

                            if (edadValidada < 0) {
                                throw new EdadInvalidaException(
                                        "La edad  no puede ser menor que cero: " + edadValidada);
                            }

                            // objeto mascota
                            Mascota nuevaMascota = new Mascota(
                                    txtNom.getText().trim(),
                                    txtEspecie.getText().trim(),
                                    edadValidada,
                                    txtPropietari.getText().trim(),
                                    txtDNI.getText().trim());

                            ConnexioBD db = new ConnexioBD();
                            db.guardarMascota(nuevaMascota);

                            JOptionPane.showMessageDialog(null, "Mascota registrada correctamente");

                        } catch (NumberFormatException nfe) {
                            System.out.println(
                                    "[ERROR en la Validación] La edad introducida no es un número entero Valido.");
                            JOptionPane.showMessageDialog(null, "Error: La edad ha de ser un número entero.",
                                    "Error de datos", JOptionPane.ERROR_MESSAGE);
                        } catch (EdadInvalidaException eie) {
                            System.out.println("[ERROR en la validacion] " + eie.getMessage());
                            JOptionPane.showMessageDialog(null, "Error: " + eie.getMessage(), "Error de datos",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (SQLException ex) {
                            System.out.println(
                                    "[ERROR Base de datos] Error critico: No se ha podido conectar con  el servidor de la clínica.");
                            JOptionPane.showMessageDialog(null,
                                    "Error : No se ha podido conectar con  el servidor de la clínica.", "Error BD",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
        }

        JPanel panelBusqueda = new JPanel(new GridLayout(3, 1, 5, 5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Busqueda y  Operacion de Gestion"));

        // Buscar por DNI
        JPanel fila1 = new JPanel();
        fila1.add(new JLabel("DNI:"));
        JTextField txtBuscaDNI = new JTextField(10);
        fila1.add(txtBuscaDNI);
        JButton btnBuscar = new JButton("Buscar por DNI");
        fila1.add(btnBuscar);

        // Modificar y Dar de Baja
        JPanel fila2 = new JPanel();
        JButton btnModificar = new JButton("Modificar datos");
        JButton btnBaja = new JButton("Dar de baja");
        JButton btnBuscarEdad = new JButton("Buscar por Edad");
        fila2.add(btnModificar);
        fila2.add(btnBaja);
        fila2.add(btnBuscarEdad);

        // botones de Ficheros TXT
        JPanel fila3 = new JPanel();
        JButton cargarBoton = new JButton("Cargar desde  TXT");
        JButton exportarBoton = new JButton("Exportar a TXT");
        fila3.add(cargarBoton);
        fila3.add(exportarBoton);

        // filas al contenedor de búsqueda
        panelBusqueda.add(fila1);
        panelBusqueda.add(fila2);
        panelBusqueda.add(fila3);

        // Área de resultados inferior
        JTextArea areaResultados = new JTextArea(10, 30);
        areaResultados.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        // Buscar por DNI
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dniABuscar = txtBuscaDNI.getText().trim();
                if (dniABuscar.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Escriba  un DNI para  Buscar.");
                    return;
                }
                try {
                    ConnexioBD db = new ConnexioBD();
                    String resultado = db.BuscarPorDni(dniABuscar);
                    areaResultados.setText(resultado);
                } catch (SQLException ex) {
                    System.out.println("ERROR - BD Error..... en la Busqueda.");
                    JOptionPane.showMessageDialog(null, "Error al conectar con  la base de datos.", "Error BD",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Modificar datos
        btnModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int edadValidada = Integer.parseInt(txtEdat.getText().trim());
                    if (edadValidada < 0)
                        throw new EdadInvalidaException("La edad  no puede ser negativa.");

                    // Creacion del objeto Mascota con los datos modificados del formulario superior
                    Mascota mascotaModificada = new Mascota(
                            txtNom.getText().trim(),
                            txtEspecie.getText().trim(),
                            edadValidada,
                            txtPropietari.getText().trim(),
                            txtDNI.getText().trim() // Se basara en este DNI para hacer el una Actualizacion
                    );

                    ConnexioBD db = new ConnexioBD();
                    db.ModificarDatosMascota(mascotaModificada);
                    JOptionPane.showMessageDialog(null, "Datos de la mascota actualiados correctamente.");
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "La edad  ha de ser un número entero  por  modificar.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (EdadInvalidaException eie) {
                    JOptionPane.showMessageDialog(null, eie.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    System.out.println("ERROR - BD " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Error en modificar: " + ex.getMessage(), "Error BD",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Dar de baja  (Usando DarDeBajaMascota)
        btnBaja.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dniBaja = txtDNI.getText().trim();
                if (dniBaja.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Escriba por favor el DNI en el formulari de arriba  para  eliminar el registro");
                    return;
                }
                int confirmar = JOptionPane.showConfirmDialog(null,
                        "Seguro que quiere dar de baja la mascota con DNI " + dniBaja + "?", "Confirmar baja",
                        JOptionPane.YES_NO_OPTION);
                if (confirmar == JOptionPane.YES_OPTION) {
                    try {

                        Mascota mascotaBaja = new Mascota("", "", 0, "", dniBaja);
                        ConnexioBD db = new ConnexioBD();
                        db.DarDeBajaMascota(mascotaBaja);
                        JOptionPane.showMessageDialog(null, "Mascota dada de baja con exito.");

                        // Limpiamos los campos
                        txtNom.setText("");
                        txtEspecie.setText("");
                        txtEdat.setText("");
                        txtPropietari.setText("");
                        txtDNI.setText("");
                    } catch (SQLException ex) {
                        System.out.println("ERROR - BD " + ex.getMessage());
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Buscar POR EDAD ....Uso buscarMascotasPorEdad....
        btnBuscarEdad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String edadTexto = txtEdat.getText().trim();
                    if (edadTexto.isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "Escribe  una edad en el campo de arriba  del formulario superior para buscvar.");
                        return;
                    }
                    int edadBuscar = Integer.parseInt(edadTexto);
                    if (edadBuscar < 0)
                        throw new EdadInvalidaException("La edad no puede  ser negativa.");

                    ConnexioBD db = new ConnexioBD();
                    String resultados = db.BuscarMascotasPorEdad(edadBuscar);
                    areaResultados.setText(resultados);

                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "La edad  ha de ser un número entero.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (EdadInvalidaException eie) {
                    JOptionPane.showMessageDialog(null, eie.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    System.out.println("ERROR - BD " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Error en la busqueda  por edad.", "Error BD",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //  Cargar desde TXT
        cargarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader("mascota_import.txt"));
                    String nom = br.readLine();
                    String especie = br.readLine();
                    String edatStr = br.readLine();
                    String propietari = br.readLine();
                    String dni = br.readLine();

                    if (nom != null && especie != null && edatStr != null && propietari != null && dni != null) {
                        txtNom.setText(nom.trim());
                        txtEspecie.setText(especie.trim());
                        txtEdat.setText(edatStr.trim());
                        txtPropietari.setText(propietari.trim());
                        txtDNI.setText(dni.trim());
                        JOptionPane.showMessageDialog(null, "Campos llenos  desde el  fichero!");
                    }
                } catch (FileNotFoundException ex) {
                    System.out.println(
                            "ERROR  No se ha  podido leer el fichero: mascota_import.txt (No existe)");
                    JOptionPane.showMessageDialog(null, "Error: El fichero 'mascota_import.txt' no existe.",
                            "Fichero no encontrado", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    System.out.println(" Error en leer el fichero: " + ex.getMessage());
                } finally {
                    try {
                        if (br != null)
                            br.close();
                    } catch (IOException ex) {
                        System.out.println("Error al cerrar el BufferedReader: " + ex.getMessage());
                    }
                }
            }
        });

        // Exportar mascota a txt
        exportarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dniABuscar = txtBuscaDNI.getText().trim();
                if (dniABuscar.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Escriba  un DNI al campo de buscar  para exportar.");
                    return;
                }

                BufferedWriter bw = null;
                try {
                    ConnexioBD db = new ConnexioBD();

                    String info = db.BuscarPorDni(dniABuscar);

                    if (info.contains("No se encontró")) {
                        JOptionPane.showMessageDialog(null,
                                "No se ha encontrado ninguna mascota con  este  DNI para exportar.");
                        return;
                    }

                    FileWriter fw = new FileWriter("mascota_export.txt", false);
                    bw = new BufferedWriter(fw);
                    bw.write(info);
                    JOptionPane.showMessageDialog(null, "Mascota exportada correctamente a 'mascota_export.txt'!");

                } catch (SQLException ex) {
                    System.out.println("ERROR - BD No se ha podido conectar.");
                } catch (IOException ex) {
                    System.out.println("ERROR  Error en escribir: " + ex.getMessage());
                } finally {
                    try {
                        if (bw != null)
                            bw.close();
                    } catch (IOException ex) {
                        System.err.println("Error al cerrar  el BufferedWriter: " + ex.getMessage());
                    }
                }
            }
        });

        // ensamblado final de la ventana
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.add(panelBusqueda, BorderLayout.NORTH);
        panelInferior.add(scrollPane, BorderLayout.CENTER);

        add(panelFormulario, BorderLayout.NORTH);
        add(panelInferior, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }
}