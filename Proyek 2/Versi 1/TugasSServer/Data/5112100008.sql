select 
mahasiswa.nrp, 
mahasiswa.nama, 
frs.semester, 
frs.tahun 
from 
frs,
mahasiswa,
matakuliah 
where 
frs.nrp=mahasiswa.nrp and 
frs.id_mk=matakuliah.id_mk and
matakuliah.mata_kuliah='BASIS DATA' and
mahasiswa.nrp like '5100%' and
(frs.semester='2' or frs.semester='4' or frs.semester='6' or frs.semester='8');

select
mahasiswa.nrp,
mahasiswa.nama,
sum(nilai.nilai_angka*matakuliah.sks)/sum(matakuliah.sks) 
from 
mahasiswa,
frs,
matakuliah,
nilai 
where 
mahasiswa.nrp=frs.nrp and 
frs.id_mk=matakuliah.id_mk and 
frs.nilai_huruf=nilai.nilai_huruf and 
(frs.semester='2' or frs.semester='4' or frs.semester='6' or frs.semester='8') and 
frs.tahun='2001' 
group by 
mahasiswa.nrp,mahasiswa.nama 
order by sum(nilai.nilai_angka*matakuliah.sks)/sum(matakuliah.sks) desc;

select 
mahasiswa.nrp,
mahasiswa.nama,
sum(nilai.nilai_angka*matakuliah.sks)/sum(matakuliah.sks) as IPS
from mahasiswa,frs,matakuliah,nilai 
where
mahasiswa.nrp=frs.nrp and 
frs.id_mk=matakuliah.id_mk and 
frs.nilai_huruf=nilai.nilai_huruf and 
(frs.semester='1' or frs.semester='3') and 
frs.tahun='2002'
group by 
mahasiswa.nrp,mahasiswa.nama 
having sum(nilai.nilai_angka*matakuliah.sks)/sum(matakuliah.sks)>(
select avg(sum(nilai.nilai_angka*matakuliah.sks)/sum(matakuliah.sks)) from frs,matakuliah,nilai where
frs.id_mk=matakuliah.id_mk and 
frs.nilai_huruf=nilai.nilai_huruf and 
frs.nrp like '5198%' and 
frs.tahun='2002' and 
(frs.semester=1 or frs.semester=3) 
group by frs.nrp)
order by IPS desc;
