output "aws_ami_id" {
  value = data.aws_ami.latest-amz-image.id
}

output "aws_ami_ip" {
  value = aws_instance.infx-server.public_ip
}