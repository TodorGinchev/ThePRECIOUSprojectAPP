%prepare environment
clc
format compact
%read file and format information
disp('Reading steps file')
fid = fopen('wearable_stepsKeegan25Aug2016.txt'); %
tline = fgets(fid);
i=1;
while ischar(tline)
    tline = tline(1:end-2);
    tline = strrep(tline, ';', ' ');
    %disp(tline)
    data(i,:)=strread(tline);
    date(i,:) = datestr(data(i,2)/60/60/24/1000 + datenum(1970,1,1));
    i=i+1;
    %data(i)=strread(line);

    tline = fgets(fid);
end
%save information ro file
disp('Saving information to file')
fid= fopen('wearable_steps_formatted.txt','w');

fprintf(fid,'%s %d \n',date(1,:),data(1,1));
for j=2:i-1
    fprintf(fid,'%s %d\t%10.0f\n',date(j,:),data(j,1),(data(j,2)-data(j-1,2))/1000/60);
end
fclose(fid);

disp('done')



% for i=1:30
%     fscanf(fileID,formatSpec)%Read the file data, filling output array, A, in column order. fscanf reapplies the format, formatSpec, throughout the file.
%     data(i)=strread(line);
% end