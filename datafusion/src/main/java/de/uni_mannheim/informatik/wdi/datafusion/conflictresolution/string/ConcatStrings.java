package de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.string;


import java.util.ArrayList;
import java.util.Collection;


import java.util.List;

import de.uni_mannheim.informatik.wdi.Matchable;
import de.uni_mannheim.informatik.wdi.datafusion.Fusable;
import de.uni_mannheim.informatik.wdi.datafusion.FusableValue;
import de.uni_mannheim.informatik.wdi.datafusion.FusedValue;
import de.uni_mannheim.informatik.wdi.datafusion.conflictresolution.ConflictResolutionFunction;

/**
 * Longest string conflict resolution: Returns the longest string value
 * @author Oliver
 *
 * @param <RecordType>
 */
public class ConcatStrings<RecordType extends Matchable & Fusable> extends ConflictResolutionFunction<String, RecordType> {

	@Override
	public FusedValue<String, RecordType> resolveConflict(Collection<FusableValue<String, RecordType>> values) {
		ArrayList<String>allValues=new ArrayList<String> () ;
		String concat="";
	
		
		for(FusableValue<String, RecordType> value : values) {
	
			if(!allValues.contains(value.getValue())){
			allValues.add(value.getValue());
			
			
			if (concat!="")
			{
				
				concat += "/"+value.getValue();
			}
			else
			{
				concat+=value.getValue();
			}
		}
			
		}
		
		return new FusedValue<>(concat);
	}

}
