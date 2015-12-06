package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.list;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.uni_mannheim.informatik.wdi.Matchable;
import de.uni_mannheim.informatik.wdi.datafusion.Fusable;
import de.uni_mannheim.informatik.wdi.datafusion.FusableValue;
import de.uni_mannheim.informatik.wdi.datafusion.FusedValue;
import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.ConflictResolutionFunction;

import de.uni_mannheim.informatik.wdi.usecase.books.Authors;

public class ModifiedUnion<ValueType, RecordType extends Matchable & Fusable> extends
ConflictResolutionFunction<List<ValueType>, RecordType> {

	@Override
public FusedValue<List<ValueType>, RecordType> resolveConflict(
	Collection<FusableValue<List<ValueType>, RecordType>> values) {

HashSet<ValueType> union = new HashSet<>();
String longestString="";
double dsScore=0.0;
List<ValueType>longestStringValue = null;

for (FusableValue<List<ValueType>, RecordType> value : values) {


	
	

	
//	if( ((Authors)value.getValue().get(0)).getAuthorName().length()>longestString.length())
//	{
//	if (value.getDataSourceScore()>dsScore)
//	dsScore=value.getDataSourceScore();
	if (value.getDataset().getRandomRecord().getIdentifier().contains("DbpediaAuthors"))
	longestStringValue=value.getValue();
	

//	}
	
	
	

	
}

union.addAll(longestStringValue);

List<ValueType> list = new LinkedList<>(union);
FusedValue<List<ValueType>, RecordType> fused = new FusedValue<>(list);

for (FusableValue<List<ValueType>, RecordType> value : values) {

	fused.addOriginalRecord(value);
}

return fused;
}

}
