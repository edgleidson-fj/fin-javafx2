package gui.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

// Classe utilitária (Utils.java).
public class Utils {

	// Função para retornar o Stage atual.
	public static Stage stageAtual(ActionEvent evento) {
		// Necessário fazer os Cast (Stage e Node).
		// O código do evento vai está dentro do parenteses junto com Cast (Node).
		return (Stage) ((Node) evento.getSource()).getScene().getWindow();
	}

	// Função para transformar String em Inteiro.
	public static Integer stringParaInteiro(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	// Função para transformar String em Double.
		public static Double stringParaDouble(String str) {
			try {
				return Double.parseDouble(str);
			} catch (NumberFormatException ex) {
				return null;
			}
		}
	
	// Função para formatar campo com Data.
	public static <T> void formatTableColumnData(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format); // format

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}

	// Função para formatar campo com Valores Decimais (Double).
	public static <T> void formatTableColumnValorDecimais(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {

				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						Locale.setDefault(Locale.US);
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};

			return cell;
		});
	}

	// Função para formatar com (DatePicker).
	public static void formatDatePicker(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);

			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

}
