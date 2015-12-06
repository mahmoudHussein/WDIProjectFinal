package de.uni_mannheim.informatik.wdi.datafusion.usecase.books;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.wdi.datafusion.XMLFormatter;
import de.uni_mannheim.informatik.wdi.usecase.books.Authors;

public class AuthorsXMLFormatter extends XMLFormatter<Authors> {

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("Authors");
	}

	@Override
	public Element createElementFromRecord(Authors record, Document doc) {
		Element author = doc.createElement("Author");
		
		author.appendChild(createTextElement("Name", record.getAuthorName(), doc));
		author.appendChild(createTextElement("Birth_Name", record.getBirthName(), doc));
		if (record.getBirthDate()!=null)
				{
		author.appendChild(createTextElement("Birth_Date", record.getBirthDate().toString("yyyy-MM-dd"), doc));
				}
		else
		{
			author.appendChild(createTextElement("Birth_Date", "", doc));
		}
		author.appendChild(createTextElement("Birth_Place", record.getBirthPlace(), doc));
		author.appendChild(createTextElement("Occupation", record.getOccupation(), doc));
		author.appendChild(createTextElement("Writing_Genre", record.getwritingGenre(), doc));
	
		return author;
	}

}
