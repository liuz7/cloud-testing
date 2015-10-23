
export VCLOUD_HOST=10.156.74.130

export VDC_USER=dev_$VDC_NUMBER
export VDC_NAME=dbaas_dev_$VDC_NUMBER
export VDC_PASSWORD=${VDC_PASSWORD:-password}

if [ "$VDC_NUMBER" -lt "50" ]; then

export STORAGE_VNX_NAME=vcd-vnx
export STORAGE_VNX_CATALOG_NAME=dbaas_catalog_vnx

export STORAGE_SF_NAME=vcd-sf
export STORAGE_SF_CATALOG_NAME=dbaas_catalog_sf

export SERVICE_NETWORK_NAME=dbaas_data_net

else

#SRE VDCs
export STORAGE_VNX_NAME=vcd-vnx-sre
export STORAGE_VNX_CATALOG_NAME=dbaas_catalog_vnx

export SERVICE_NETWORK_NAME=dbaas_data_net_clu2

fi
