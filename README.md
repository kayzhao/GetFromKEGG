# GetFromKEGG
Get data from [KEGG](http://www.kegg.jp/) online database using jsoup jar

## Data Reptile Code

### GetDiseasesFromKEGG

	Get the Diseases Data From KEGG.
The latest KEGG-Disease Data is 20170314 version ( file path : disease/kegg/Kegg_Diseases_All_Data_20170314) 

**Data Column**

	KEGG Disease ID
	Name
	Description
	Category
	Gene
	Drug
	Env factor
	Carcinogen
	Comment
	Marker
	Reference
	Other DBs

---

### GetDrugGroupFromKEGG
	Get the Drug Group Data From KEGG

**Data Column**

	KEGG DrugGroup ID
	Name
	Member
	Remark
	Comment

---

### GetDrugsFromKEGG
	Get the Drugs Data From KEGG

**Data Column**

	KEGG Drug ID
	Name
	Formula
	Exact mass
	Mol weight
	Other DBs

---


## Data Process Code

### OMIM_to_DrugBank
	Get the OMIM-DrungBank associations 

**Pipeline**

- Get the Original Data From KEGG

		1. Get the Diseases Data
		2. Get the Drugs Data
		3. Get the DrugGroup Data

- Extract the Data Of KEGG Data

		1. Get the (disease id - drug ids - omim ids) data  from Diseases Data
		2. Get the (drug id - drugbank id) data from Drugs Data
		3. Get the (druggroup id - drug id) data from DrugGroup Data
		4. Get the OMIM IDs to DrugBank associations data
