variable "vpc_cidr_block" {
  default = "10.0.0.0/16"
}
variable "subnet_cidr_block" {
  default = "10.0.10.0/24"
}
variable "avail_zone" {
  default = "eu-central-1"
}
variable "env_prefix" {
  default = "dev"
}
variable "allowed_ips" {
  default = "5.147.48.161/32"
}
variable "instance_type" {
  default = "t2.micro"
}
variable "region" {
  default = "eu-central-1a"
}